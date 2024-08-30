package mes.app.sale.service;

import mes.config.Settings;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GeneSearchService {
	
	@Autowired
	SqlRunner sqlRunner;
	
	@Autowired
	mes.domain.repository.TB_RP320Repository TB_RP320Repository;
	
	@Autowired
	mes.domain.repository.TB_RP310Repository TB_RP310Repository;
	
	@Autowired
	Settings settings;
	
	// yoy 전년 동기간 대비 데이터 조회
	/**
	 * YoY(전년 동기 대비) 데이터를 조회하는 메서드
	 *
	 * @param powernm  발전기 ID
	 * @param startYear    비교할 첫 번째 연도 (예: 2023)
	 * @param endYear      비교할 두 번째 연도 (예: 2024)
	 * @return             연도별 월별 에너지 발전량 비교 데이터
	 */
	public List<Map<String, Object>> getYoYComparisonData(String powernm, String startYear, String endYear){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("powernm", powernm);
		params.addValue("startYear", Integer.parseInt(startYear));  // Integer로 변환
		params.addValue("endYear", Integer.parseInt(endYear));  // Integer로 변환
		
		String sql = """
        select
            to_char(to_date(t.standdt, 'yyyy-mm-dd'), 'mm') as month,
            sum(case when extract(year from to_date(t.standdt, 'yyyy-mm-dd')) = :startYear then t.mevaluet else 0 end) as value1,
            sum(case when extract(year from to_date(t.standdt, 'yyyy-mm-dd')) = :endYear then t.mevaluet else 0 end) as value2
        from
            TB_RP320 t
        where
            t.powernm = :powernm
            and extract(year from to_date(t.standdt, 'yyyy-mm-dd')) in (:startYear, :endYear)
        group by
            to_char(to_date(t.standdt, 'yyyy-mm-dd'), 'mm')
        order by
            month
        """;
		
//		return this.sqlRunner.getRows(sql, params);
		List<Map<String, Object>> result = this.sqlRunner.getRows(sql, params);
		// 디버깅을 위한 로그 추가
		System.out.println("Executed SQL: " + sql);
		System.out.println("Parameters: " + params);
		System.out.println("Result: " + result);
		
		return result;
	}
	
	
	// qoq 분기 대비 데이터 조회
	public List<Map<String, Object>> getQoQComparisonData(String powernm, String startYear, String startQuarter, String endQuarter){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("powernm", powernm);
		params.addValue("startYear", startYear);
		params.addValue("startQuarter", startQuarter);
		params.addValue("endQuarter", endQuarter);
		
		String sql = """
    SELECT
        EXTRACT(QUARTER FROM to_date(t.standdt, 'yyyy-mm-dd')) AS quarter,
        SUM(CASE WHEN EXTRACT(YEAR FROM to_date(t.standdt, 'yyyy-mm-dd')) = :startYear AND EXTRACT(QUARTER FROM to_date(t.standdt, 'yyyy-mm-dd')) = :startQuarter THEN t.mevaluet ELSE 0 END) AS value_quarter1,
        SUM(CASE WHEN EXTRACT(YEAR FROM to_date(t.standdt, 'yyyy-mm-dd')) = :startYear AND EXTRACT(QUARTER FROM to_date(t.standdt, 'yyyy-mm-dd')) = :endQuarter THEN t.mevaluet ELSE 0 END) AS value_quarter2
    FROM
        TB_RP320 t
    WHERE
        t.powernm = :powernm
        AND EXTRACT(YEAR FROM to_date(t.standdt, 'yyyy-mm-dd')) = :startYear
        AND EXTRACT(QUARTER FROM to_date(t.standdt, 'yyyy-mm-dd')) IN (:startQuarter, :endQuarter)
    GROUP BY
        EXTRACT(QUARTER FROM to_date(t.standdt, 'yyyy-mm-dd'))
    ORDER BY
        quarter
    """;
		
		return this.sqlRunner.getRows(sql, params);
	}
	
	
	
	// mom 월 대비 데이터 조회
	public List<Map<String, Object>> getMoMComparisonData(String powernm, String startMonth, String endMonth){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("powernm", powernm);
		params.addValue("startMonth", startMonth);
		params.addValue("endMonth", endMonth);
		
		String sql = """
    SELECT
        to_char(to_date(t.standdt, 'yyyy-mm-dd'), 'MM') AS month,
        SUM(CASE WHEN to_char(to_date(t.standdt, 'yyyy-mm-dd'), 'MM') = :startMonth THEN t.mevaluet ELSE 0 END) AS value_month1,
        SUM(CASE WHEN to_char(to_date(t.standdt, 'yyyy-mm-dd'), 'MM') = :endMonth THEN t.mevaluet ELSE 0 END) AS value_month2
    FROM
        TB_RP320 t
    WHERE
        t.powernm = :powernm
        AND to_char(to_date(t.standdt, 'yyyy-mm-dd'), 'MM') IN (:startMonth, :endMonth)
    GROUP BY
        to_char(to_date(t.standdt, 'yyyy-mm-dd'), 'MM')
    ORDER BY
        month
    """;
		
		return this.sqlRunner.getRows(sql, params);
	}
	
	
	
	// ytd 올해의 누적 발전량과 작년과 비교 (ex 2023년과 2024년의 누적 발전량 비교)
	public List<Map<String, Object>> getYTDComparisonData(String powernm, String startDate, String endDate){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("powernm", powernm);
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);
		
		String sql = """
    SELECT
        to_char(to_date(t.standdt, 'yyyy-mm-dd'), 'YYYY-MM-DD') AS date,
        SUM(t.mevaluet) AS total_value
    FROM
        TB_RP320 t
    WHERE
        t.powernm = :powernm
        AND to_date(t.standdt, 'yyyy-mm-dd') BETWEEN to_date(:startDate, 'yyyy-mm-dd') AND to_date(:endDate, 'yyyy-mm-dd')
    GROUP BY
        to_char(to_date(t.standdt, 'yyyy-mm-dd'), 'YYYY-MM-DD')
    ORDER BY
        date
    """;
		
		return this.sqlRunner.getRows(sql, params);
	}
	
}
