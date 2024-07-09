package mes.app.cost.service;

import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GasBillService {

    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getMonthlyUsageSummary(String startDate, String endDate) {

        MapSqlParameterSource params = new MapSqlParameterSource();

        String sql = """
                SELECT
                    DATEPART(month, INDATE) AS month,
                    SUM(SMUSEQTY) AS total_gas_usage,
                    SUM(SMUSEQTY) AS total_monthly_usage,
                    SUM(ASKAMT) AS total_billing_amount
                FROM
                    TB_RP410
                GROUP BY
                    DATEPART(month, INDATE)
                ORDER BY
                    month;
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, params);
        return items;
    }
}

