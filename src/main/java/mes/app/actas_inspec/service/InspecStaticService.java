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


    public List<Map<String, Object>> getRP710ComparisonList(String startDate, String endDate, String startHour, String endHour, String Comparison){


        if(startDate.contains("-")){
            startDate = startDate.replaceAll("-","");
        }

        if(endDate.contains("-")){
            endDate = endDate.replaceAll("-","");
        }

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql;

        if(startHour == null && endHour == null){
            dicParam.addValue("startDate", startDate);
            dicParam.addValue("endDate", endDate);

            switch(Comparison){
                case "YoY":
                    sql = """
                select checkdt, checkarea, checkusr, checkstdt || ' ~ ' || checkendt as hour   
                from tb_rp710
                where TO_DATE(checkdt, 'YYYYMMDD')
                between TO_DATE(:startDate, 'YYYYMMDD') - INTERVAL '1 year'
                AND TO_DATE(:endDate, 'YYYYMMDD') - INTERVAL '1 year'
                order by checkdt
                union all
                select checkdt, checkarea, checkusr, checkstdt || ' ~ ' || checkendt as hour   
                from tb_rp710
                where checkdt between :startDate and :endDate
                order by checkdt;
                
                """;
                break;



                default:
                    sql = """
                select checkdt, checkarea, checkusr, checkstdt || ' ~ ' || checkendt as hour   
                from tb_rp710
                where checkdt between :startDate and :endDate
                order by checkdt;
                """;

            }

        }else{
            dicParam.addValue("startDate", startDate);

            startHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + startHour + ":00:00+09";
            endHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + endHour + ":00:00+09";

            dicParam.addValue("startDate", startDate);
            dicParam.addValue("startHour", startHour);
            dicParam.addValue("endHour", endHour);

            sql = """
                select checkdt, checkarea, checkusr, checkstdt || ' ~ ' || checkendt as hour, indatem   
                from tb_rp710
                where checkdt = :startDate
                and indatem between :startHour::timestamptz and :endHour::timestamptz
                order by checkdt;      
                  """;
        }


        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }

    public List<Map<String, Object>> getRP710List(String startDate, String endDate, String startHour, String endHour){


        if(startDate.contains("-")){
            startDate = startDate.replaceAll("-","");
        }

        if(endDate.contains("-")){
            endDate = endDate.replaceAll("-","");
        }

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql;

        if(startHour == null && endHour == null){
            dicParam.addValue("startDate", startDate);
            dicParam.addValue("endDate", endDate);


            sql = """
                select checkdt, checkarea, checkusr, checkstdt || ' ~ ' || checkendt as hour   
                from tb_rp710
                where checkdt between :startDate and :endDate
                order by checkdt;
                """;
        }else{
            dicParam.addValue("startDate", startDate);

            startHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + startHour + ":00:00+09";
            endHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + endHour + ":00:00+09";

            dicParam.addValue("startDate", startDate);
            dicParam.addValue("startHour", startHour);
            dicParam.addValue("endHour", endHour);

            sql = """
                select checkdt, checkarea, checkusr, checkstdt || ' ~ ' || checkendt as hour, indatem   
                from tb_rp710
                where checkdt = :startDate
                and indatem between :startHour::timestamptz and :endHour::timestamptz
                order by checkdt;      
                  """;
        }


        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }


    public List<Map<String, Object>> getRP720List(String startDate, String endDate, String startHour, String endHour){

        if(startDate.contains("-")){
            startDate = startDate.replaceAll("-","");
        }
        if(endDate.contains("-")){
            endDate = endDate.replaceAll("-","");
        }

        MapSqlParameterSource dicParam = new MapSqlParameterSource();



        String sql;

        if(startHour == null && endHour == null){
            dicParam.addValue("startDate", startDate);
            dicParam.addValue("endDate", endDate);

            sql = """
                SELECT rp720.checkdt as checkdt, rp720.chkaddres as checkarea, rp720.checkrem as result, STRING_AGG(rp726.checkusr, ', ') AS checkusr
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
        }else{
            startHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + startHour + ":00:00+09";
            endHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + endHour + ":00:00+09";

            dicParam.addValue("startDate", startDate);
            dicParam.addValue("startHour", startHour);
            dicParam.addValue("endHour", endHour);

            sql = """
                SELECT  rp720.checkdt as checkdt, rp720.chkaddres as checkarea, rp720.checkrem as result, STRING_AGG(rp726.checkusr, ', ') AS checkusr, rp720.indatem as indatem
                FROM tb_rp720 rp720
                JOIN tb_rp726 rp726 ON rp720.spworkcd = rp726.spworkcd
                AND rp720.spplancd = rp726.spplancd
                AND rp720.spcompcd = rp726.spcompcd
                AND rp720.checkno = rp726.checkno
                AND rp720.checkdt = rp726.checkdt
                WHERE indatem between :startHour::timestamptz and :endHour::timestamptz
                GROUP BY rp720.checkdt, rp720.chkaddres, rp720.checkrem, rp720.indatem
                ORDER BY rp720.checkdt     
                  """;
        }



        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }


    public List<Map<String, Object>> getRP750List(String startDate, String endDate, String startHour, String endHour){

        if(startDate.contains("-")){
            startDate = startDate.replaceAll("-","");
        }
        if(endDate.contains("-")){
            endDate = endDate.replaceAll("-","");
        }

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql;

        if(startHour == null && endHour == null){
            dicParam.addValue("startDate", startDate);
            dicParam.addValue("endDate", endDate);

            sql = """
                select a.checkdt ,a.checkarea, b."Value" AS result, a.checktitle as title   
                from tb_rp750 a 
                JOIN user_code b ON a.endresult = b.id
                where a.checkdt between :startDate and :endDate
                order by a.checkdt;
                """;
        }else{
            dicParam.addValue("startDate", startDate);

            startHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + startHour + ":00:00+09";
            endHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + endHour + ":00:00+09";

            dicParam.addValue("startDate", startDate);
            dicParam.addValue("startHour", startHour);
            dicParam.addValue("endHour", endHour);

            sql = """
                select a.checkdt ,a.checkarea, b."Value" AS result, a.checktitle as title, a.indatem as indatem   
                from tb_rp750 a 
                JOIN user_code b ON a.endresult = b.id
                where indatem between :startHour::timestamptz and :endHour::timestamptz
                order by a.checkdt;
                """;
        }

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }


    public List<Map<String, Object>> getRP770List(String startDate, String endDate, String startHour, String endHour){

        if(startDate.contains("-")){
            startDate = startDate.replaceAll("-","");
        }
        if(endDate.contains("-")){
            endDate = endDate.replaceAll("-","");
        }

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql;
        if(startHour == null && endHour == null){

            dicParam.addValue("startDate", startDate);
            dicParam.addValue("endDate", endDate);

            sql = """
                select TRAINDT, TRAINAREA, TRAINUSR, TRAINNM   
                from tb_rp770 
                where traindt between :startDate and :endDate
                order by traindt;
                """;
        }else{
            dicParam.addValue("startDate", startDate);

            startHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + startHour + ":00:00+09";
            endHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + endHour + ":00:00+09";

            dicParam.addValue("startDate", startDate);
            dicParam.addValue("startHour", startHour);
            dicParam.addValue("endHour", endHour);

            sql = """
                select TRAINDT, TRAINAREA, TRAINUSR, TRAINNM, indatem   
                from tb_rp770 
                where indatem between :startHour::timestamptz and :endHour::timestamptz
                order by traindt;
                """;
        }


        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }


    public List<Map<String, Object>> getRP780List(String startDate, String endDate, String startHour, String endHour){

        if(startDate.contains("-")){
            startDate = startDate.replaceAll("-","");
        }
        if(endDate.contains("-")){
            endDate = endDate.replaceAll("-","");
        }

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql;

        if(startHour == null && endHour == null){
            dicParam.addValue("startDate", startDate);
            dicParam.addValue("endDate", endDate);

            sql = """
                select TRAINDT, TRAINAREA, TRAINUSR, TRAINNM
                from tb_rp780 
                where traindt between :startDate and :endDate
                order by traindt;
                """;
        }else{
            dicParam.addValue("startDate", startDate);

            startHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + startHour + ":00:00+09";
            endHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + endHour + ":00:00+09";

            dicParam.addValue("startDate", startDate);
            dicParam.addValue("startHour", startHour);
            dicParam.addValue("endHour", endHour);

            sql = """
                select TRAINDT, TRAINAREA, TRAINUSR, TRAINNM, indatem      
                from tb_rp780 
                where indatem between :startHour::timestamptz and :endHour::timestamptz
                order by traindt;
                """;
        }

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }



    public List<Map<String, Object>> getRP810List(String startDate, String endDate, String startHour, String endHour){


        if(startDate.contains("-")){
            startDate = startDate.replaceAll("-","");
        }

        if(endDate.contains("-")){
            endDate = endDate.replaceAll("-","");
        }

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql;

        if(startHour == null && endHour == null){
            dicParam.addValue("startDate", startDate);
            dicParam.addValue("endDate", endDate);


            sql = """
                select TO_CHAR(indatem, 'YYYYMMDD') as checkdt, sitename, purpvisit, fsresponnm, indatem   
                from tb_rp810
                where TO_CHAR(indatem, 'YYYYMMDD') between :startDate and :endDate
                order by checkdt;
                """;
        }else{
            dicParam.addValue("startDate", startDate);

            startHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + startHour + ":00:00+09";
            endHour = startDate.substring(0,4) + "-" + startDate.substring(4,6) + "-" + startDate.substring(6,8) + " " + endHour + ":00:00+09";

            dicParam.addValue("startDate", startDate);
            dicParam.addValue("startHour", startHour);
            dicParam.addValue("endHour", endHour);

            sql = """
                select TO_CHAR(indatem, 'YYYYMMDD') as checkdt, sitename, purpvisit, fsresponnm, indatem   
                from tb_rp810
                where TO_CHAR(indatem, 'YYYYMMDD') = :startDate
                and indatem between :startHour::timestamptz and :endHour::timestamptz
                order by checkdt;      
                  """;
        }


        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }

}
