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
                order by checkdt;
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }


    public List<Map<String, Object>> getRP720List(String startDate, String endDate){

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
                SELECT  rp720.chkaddres as checkarea, rp720.checkrem as checkrem, STRING_AGG(rp726.checkusr, ', ') AS checkusr
                FROM tb_rp720 rp720
                JOIN tb_rp726 rp726 ON rp720.spworkcd = rp726.spworkcd
                AND rp720.spplancd = rp726.spplancd
                AND rp720.spcompcd = rp726.spcompcd
                AND rp720.checkno = rp726.checkno
                AND rp720.checkdt = rp726.checkdt
                where rp720.checkdt between :startDate and :endDate
                GROUP BY rp720.checkdt, rp720.chkaddres, rp720.checkrem
                ORDER BY rp720.checkdt
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }


    public List<Map<String, Object>> getRP750List(String startDate, String endDate){

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
                select a.checkarea, b."Value" AS checkresult, a.checktitle   
                from tb_rp750 a 
                JOIN user_code b ON a.endresult = b.id
                where a.checkdt between :startDate and :endDate
                order by a.checkdt;
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }



}
