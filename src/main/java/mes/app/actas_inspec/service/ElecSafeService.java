package mes.app.actas_inspec.service;

import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ElecSafeService {

    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getInspecList(String searchusr, String startDate, String endDate) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        dicParam.addValue("paramusr", "%" +searchusr+ "%");

        String sql = """
                select 
                ROW_NUMBER() OVER (ORDER BY indatem DESC) AS rownum,
                *
                from tb_rp750
                order by indatem desc
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }
}
