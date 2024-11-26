package mes.app.cost.service;

import mes.domain.entity.TB_RP520;
import mes.domain.repository.TB_RP520Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
public class CostService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_RP520Repository RP520Repository;


    public List<Map<String, Object>> getCostUploadList(){


        MapSqlParameterSource params = new MapSqlParameterSource();

        // 현재 연도 구하기
        int currentYear = LocalDate.now().getYear();
        params.addValue("currentYear", currentYear);

        String sql = """
                  SELECT *
                        FROM TB_RP520 AS RP520
                        WHERE YEAR(your_date_column) = :currentYear
                        and 1 = 1
                """;

        List<Map<String,Object>> items = this.sqlRunner.getRows(sql,params);
        return items;
    }


    public List<Map<String, Object>> getCostSave(TB_RP520 tbRp520){
        MapSqlParameterSource params = new MapSqlParameterSource();
        int currentYear = LocalDate.now().getYear();
        params.addValue("currentYear", currentYear);


        String sql = """
                
                """;

        List<Map<String,Object>> items;
        try{
            items = this.sqlRunner.getRows(sql, params);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Sql 실행오류: "+ e.getMessage());
        }
        return items;
    }

}
