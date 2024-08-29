package mes.app.actas_inspec.service;


import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InspecStaticService {

    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getRP710List(String startDate, String endDate){

        if(startDate.contains("-")){
            startDate = startDate.replaceAll("-","");
        }
        if(endDate.contains("-")){
            endDate = endDate.replaceAll("-","");
        }

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("startDate", startDate);
        dicParam.addValue("endDate", endDate);


        String sql;

        sql = """
                select checkarea, checkusr, checkstdt || ' ~ ' || checkendt as hour   
                from tb_rp710
                where checkdt between :startDate and :endDate
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }

}
