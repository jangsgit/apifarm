package mes.app.sale.service;

import mes.domain.repository.TB_RP320Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneStatisticsService {

	@Autowired
	private TB_RP320Repository tb_rp320Repository;

	@Autowired
	private SqlRunner sqlRunner;

	public Map<String, Object> getPeriodicData(String startYearMonth, String endYearMonth, String powerid) {
		// 입력받은 년-월 문자열을 YearMonth 객체로 변환
		YearMonth start = YearMonth.parse(startYearMonth);
		YearMonth end = YearMonth.parse(endYearMonth);

		// 시작 날짜를 해당 월의 첫날로, 종료 날짜를 해당 월의 마지막 날로 설정
		String startDate = start.atDay(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String endDate = end.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		System.out.println("Converted date range: " + startDate + " to " + endDate);

		// 각 기간별 데이터 계산
		Map<String, Object> result = new HashMap<>();
		result.put("monthly", calculatePeriodicData(startDate, endDate, powerid, "YYYY-MM", "월별"));
		result.put("quarterly", calculatePeriodicData(startDate, endDate, powerid, "YYYY-Q", "분기별"));
		result.put("halfYearly", calculatePeriodicData(startDate, endDate, powerid, "YYYY-HH", "반기별"));
		result.put("yearly", calculatePeriodicData(startDate, endDate, powerid, "YYYY", "연도별"));

		return result;
	}

	public Map<String, Object> getComparativeData(String startDate, String endDate, String powerid) {
		Map<String, Object> result = new HashMap<>();
		result.put("yoy", getYoYData(startDate, endDate, powerid));
		result.put("qoq", getQoQData(startDate, endDate, powerid));
		result.put("mom", getMoMData(startDate, endDate, powerid));
		result.put("ytd", getYTDData(startDate, endDate, powerid));
		return result;
	}

	//////
	private List<Map<String, Object>> calculatePeriodicData(String startDate, String endDate, String powerid, String dateFormat, String periodType) {
		StringBuilder sql = new StringBuilder("SELECT period, powernm, SUM(mevalue) as totalValue FROM (");
		sql.append("SELECT ");

		if ("분기별".equals(periodType)) {
			sql.append("TO_CHAR(t.standdt::date, 'YYYY') || '-Q' || TO_CHAR(t.standdt::date, 'Q') as period, ");
		} else if ("반기별".equals(periodType)) {
			sql.append("TO_CHAR(t.standdt::date, 'YYYY') || '-H' || CASE WHEN CAST(TO_CHAR(t.standdt::date, 'MM') AS INTEGER) <= 6 THEN '1' ELSE '2' END as period, ");
		} else {
			sql.append("TO_CHAR(t.standdt::date, :dateFormat) as period, ");
		}

		sql.append("t.powernm, t.mevalue " +
				   "FROM tb_rp320 t " +
				   "WHERE t.standdt BETWEEN :startDate AND :endDate ");

		if (powerid != null && !powerid.equals("all")) {
			sql.append("AND t.powerid = :powerid ");
		}

		sql.append(") AS subquery ");
		sql.append("GROUP BY period, powernm ");
		sql.append("ORDER BY period, powernm");

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);
		params.addValue("dateFormat", dateFormat);
		if (powerid != null && !powerid.equals("all")) {
			params.addValue("powerid", powerid);
		}

		return sqlRunner.getRows(sql.toString(), params);
	}



	// 월별 데이터 계산 로직
	private List<Map<String, Object>> getMonthlyData(String startDate, String endDate, String powerid) {
		StringBuilder sql = new StringBuilder(
				"SELECT TO_CHAR(TO_DATE(t.standdt, 'YYYYMMDD'), 'YYYY-MM') as month, " +
				"t.powernm, SUM(t.mevalue) as totalValue " +
				"FROM tb_rp320 t " +
				"WHERE t.standdt BETWEEN :startDate AND :endDate ");

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);

		if (powerid != null && !powerid.equals("all")) {
			sql.append("AND t.powerid = :powerid ");
			params.addValue("powerid", powerid);
		}

		sql.append("GROUP BY TO_CHAR(TO_DATE(t.standdt, 'YYYYMMDD'), 'YYYY-MM'), t.powernm " +
				   "ORDER BY month, t.powernm");

		return sqlRunner.getRows(sql.toString(), params);
	}

	// 분기별 데이터 계산 로직
	private List<Map<String, Object>> getQuarterlyData(String startDate, String endDate, String powerid) {
		String sql = "SELECT EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')) as year, " +
					 "CEIL(EXTRACT(MONTH FROM TO_DATE(t.standdt, 'YYYYMMDD')) / 3.0) as quarter, " +
					 "t.powernm, SUM(t.mevalue) as totalValue " +
					 "FROM tb_rp320 t " +
					 "WHERE t.standdt BETWEEN :startDate AND :endDate " +
					 (powerid != null ? "AND t.powerid = :powerid " : "") +
					 "GROUP BY EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')), " +
					 "CEIL(EXTRACT(MONTH FROM TO_DATE(t.standdt, 'YYYYMMDD')) / 3.0), t.powernm " +
					 "ORDER BY year, quarter, t.powernm";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);
		if (powerid != null) {
			params.addValue("powerid", powerid);
		}

		return sqlRunner.getRows(sql, params);
	}

	// 반기별 데이터 계산 로직
	private List<Map<String, Object>> getHalfYearlyData(String startDate, String endDate, String powerid) {
		String sql = "SELECT EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')) as year, " +
					 "CEIL(EXTRACT(MONTH FROM TO_DATE(t.standdt, 'YYYYMMDD')) / 6.0) as half, " +
					 "t.powernm, SUM(t.mevalue) as totalValue " +
					 "FROM tb_rp320 t " +
					 "WHERE t.standdt BETWEEN :startDate AND :endDate " +
					 (powerid != null ? "AND t.powerid = :powerid " : "") +
					 "GROUP BY EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')), " +
					 "CEIL(EXTRACT(MONTH FROM TO_DATE(t.standdt, 'YYYYMMDD')) / 6.0), t.powernm " +
					 "ORDER BY year, half, t.powernm";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);
		if (powerid != null) {
			params.addValue("powerid", powerid);
		}

		return sqlRunner.getRows(sql, params);
	}

	// 연도별 데이터 계산 로직
	private List<Map<String, Object>> getYearlyData(String startDate, String endDate, String powerid) {
		String sql = "SELECT EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')) as year, " +
					 "t.powernm, SUM(t.mevalue) as totalValue " +
					 "FROM tb_rp320 t " +
					 "WHERE t.standdt BETWEEN :startDate AND :endDate " +
					 (powerid != null ? "AND t.powerid = :powerid " : "") +
					 "GROUP BY EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')), t.powernm " +
					 "ORDER BY year, t.powernm";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);
		if (powerid != null) {
			params.addValue("powerid", powerid);
		}

		return sqlRunner.getRows(sql, params);
	}

	// YoY 데이터 계산 로직
	private List<Map<String, Object>> getYoYData(String startDate, String endDate, String powerid) {
		String sql = "WITH yearly_data AS (" +
					 "  SELECT EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')) as year, " +
					 "  t.powernm, SUM(t.mevalue) as totalValue " +
					 "  FROM tb_rp320 t " +
					 "  WHERE t.standdt BETWEEN :startDate AND :endDate " +
					 (powerid != null ? "  AND t.powerid = :powerid " : "") +
					 "  GROUP BY EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')), t.powernm" +
					 ") " +
					 "SELECT d.year, d.powernm, " +
					 "  (d.totalValue - LAG(d.totalValue) OVER (PARTITION BY d.powernm ORDER BY d.year)) / LAG(d.totalValue) OVER (PARTITION BY d.powernm ORDER BY d.year) * 100 as yoy_change " +
					 "FROM yearly_data d " +
					 "ORDER BY d.year, d.powernm";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);
		if (powerid != null) {
			params.addValue("powerid", powerid);
		}

		return sqlRunner.getRows(sql, params);
	}

	// QoQ 데이터 계산 로직
	private List<Map<String, Object>> getQoQData(String startDate, String endDate, String powerid) {
		String sql = "WITH quarterly_data AS (" +
					 "  SELECT EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')) as year, " +
					 "  CEIL(EXTRACT(MONTH FROM TO_DATE(t.standdt, 'YYYYMMDD')) / 3.0) as quarter, " +
					 "  t.powernm, SUM(t.mevalue) as totalValue " +
					 "  FROM tb_rp320 t " +
					 "  WHERE t.standdt BETWEEN :startDate AND :endDate " +
					 (powerid != null ? "  AND t.powerid = :powerid " : "") +
					 "  GROUP BY EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')), " +
					 "  CEIL(EXTRACT(MONTH FROM TO_DATE(t.standdt, 'YYYYMMDD')) / 3.0), t.powernm" +
					 ") " +
					 "SELECT d.year, d.quarter, d.powernm, " +
					 "  (d.totalValue - LAG(d.totalValue) OVER (PARTITION BY d.powernm ORDER BY d.year, d.quarter)) / LAG(d.totalValue) OVER (PARTITION BY d.powernm ORDER BY d.year, d.quarter) * 100 as qoq_change " +
					 "FROM quarterly_data d " +
					 "ORDER BY d.year, d.quarter, d.powernm";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);
		if (powerid != null) {
			params.addValue("powerid", powerid);
		}

		return sqlRunner.getRows(sql, params);
	}

	// MoM 데이터 계산 로직
	private List<Map<String, Object>> getMoMData(String startDate, String endDate, String powerid) {
		String sql = "WITH monthly_data AS (" +
					 "  SELECT TO_CHAR(TO_DATE(t.standdt, 'YYYYMMDD'), 'YYYY-MM') as month, " +
					 "  t.powernm, SUM(t.mevalue) as totalValue " +
					 "  FROM tb_rp320 t " +
					 "  WHERE t.standdt BETWEEN :startDate AND :endDate " +
					 (powerid != null ? "  AND t.powerid = :powerid " : "") +
					 "  GROUP BY TO_CHAR(TO_DATE(t.standdt, 'YYYYMMDD'), 'YYYY-MM'), t.powernm" +
					 ") " +
					 "SELECT d.month, d.powernm, " +
					 "  (d.totalValue - LAG(d.totalValue) OVER (PARTITION BY d.powernm ORDER BY d.month)) / LAG(d.totalValue) OVER (PARTITION BY d.powernm ORDER BY d.month) * 100 as mom_change " +
					 "FROM monthly_data d " +
					 "ORDER BY d.month, d.powernm";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);
		if (powerid != null) {
			params.addValue("powerid", powerid);
		}

		return sqlRunner.getRows(sql, params);
	}

	// YTD 데이터 계산 로직
	private List<Map<String, Object>> getYTDData(String startDate, String endDate, String powerid) {
		String sql = "SELECT TO_CHAR(TO_DATE(t.standdt, 'YYYYMMDD'), 'YYYY-MM') as month, " +
					 "t.powernm, " +
					 "SUM(SUM(t.mevalue)) OVER (PARTITION BY t.powernm, EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')) ORDER BY TO_CHAR(TO_DATE(t.standdt, 'YYYYMMDD'), 'YYYY-MM')) as cumulativeValue " +
					 "FROM tb_rp320 t " +
					 "WHERE t.standdt BETWEEN :startDate AND :endDate " +
					 (powerid != null ? "AND t.powerid = :powerid " : "") +
					 "GROUP BY TO_CHAR(TO_DATE(t.standdt, 'YYYYMMDD'), 'YYYY-MM'), t.powernm, EXTRACT(YEAR FROM TO_DATE(t.standdt, 'YYYYMMDD')) " +
					 "ORDER BY month, t.powernm";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);
		if (powerid != null) {
			params.addValue("powerid", powerid);
		}

		return sqlRunner.getRows(sql, params);
	}
}
