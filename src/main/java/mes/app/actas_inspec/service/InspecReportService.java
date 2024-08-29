package mes.app.actas_inspec.service;


import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import java.util.*;



@Service
public class InspecReportService {


    @Autowired
    SqlRunner sqlRunner;




    public List<Map<String, Object>> getCalenderList(){
        MapSqlParameterSource dicParam = new MapSqlParameterSource();


        String sql = """
                    SELECT checkdt, COUNT(*) AS count, '순회점검' AS inspection_type
                                                           FROM tb_rp710
                                                           GROUP BY checkdt
                                                           UNION ALL
                    SELECT checkdt, COUNT(*) AS count, '전기안전점검' AS inspection_type
                                                           FROM tb_rp750
                                                           GROUP BY checkdt
                                                           UNION ALL
                    SELECT checkdt, COUNT(*) AS count, '합동안전점검' AS inspection_type
                                                           FROM tb_rp720
                                                           GROUP BY checkdt
                                                           ORDER BY checkdt;
                """;
        return this.sqlRunner.getRows(sql, dicParam);

    }

    public List<Map<String, Object>> getStatusList(String frdate, String todate) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        dicParam.addValue("startDate", frdate);
        dicParam.addValue("endDate", todate);

        String sql = """
                    select count(*) as total_count,
                    		sum(case when flag = 'N' then 1 else 0 end) as flag_n_count,
                    		sum(case when flag = 'Y' then 1 else 0 end) as flag_y_count
                    from tb_rp710
                    where checkdt between :startDate and :endDate;
                """;
        return this.sqlRunner.getRows(sql, dicParam);
    }

    public List<Map<String, Object>> getInspecList(String date) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        dicParam.addValue("date", date);
        //순회점검
        String sql = """
                   SELECT checkdt, 'wm_inspec_month_list' AS inspectypeCode, '순회점검' AS inspectype, checkarea as inspecarea, checkusr\s
                   FROM tb_rp710
                   where checkdt = :date
                """;
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        //전기안전
        String sql2 = """
                    SELECT checkdt, 'wm_elecsafe_input' AS inspectypeCode, '전기안전점검' AS inspectype, checkarea as inspecarea, checkusr
                    FROM tb_rp750
                    where checkdt = :date  
                """;
        List<Map<String, Object>> items2 = this.sqlRunner.getRows(sql2, dicParam);
        //합동안전
        String sql3 = """
                    SELECT  rp720.checkdt as checkdt, rp720.chkaddres as inspecarea, 'wm_hap_input' AS inspectypeCode, '합동안전점검' AS inspectype, STRING_AGG(rp726.checkusr, ', ') AS checkusr
                    FROM tb_rp720 rp720
                    JOIN tb_rp726 rp726
                    ON rp720.spworkcd = rp726.spworkcd
                           AND rp720.spplancd = rp726.spplancd
                           AND rp720.spcompcd = rp726.spcompcd
                           AND rp720.checkno = rp726.checkno
                           AND rp720.checkdt = rp726.checkdt
                    where rp720.checkdt = :date       
                    GROUP BY rp720.checkdt, rp720.chkaddres       
                """;
        List<Map<String, Object>> items3 = this.sqlRunner.getRows(sql3, dicParam);


        items.addAll(items2);
        items.addAll(items3);


        return items;
    }

    public List<Map<String, Object>> getInspecMonthList(String frdate, String todate) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("frdate", frdate);
        dicParam.addValue("todate", todate);


        String sql = """
                    SELECT 'wm_inspec_month_list' as inspectypeCode, 
                    checkdt, '순회점검' AS inspectype, checkarea as inspecarea, TO_CHAR(CAST("checkstdt" AS TIMESTAMP), 'HH24:MI') || ' ~ ' || TO_CHAR(CAST("checkendt" AS TIMESTAMP), 'HH24:MI') AS checkhour
                    FROM tb_rp710
                    where checkdt between :frdate and :todate
                    UNION ALL
                    SELECT 'wm_elecsafe_input' as inspectypeCode,  
                    checkdt, '전기안전점검' AS inspectype, checkarea as inspecarea, '' as checkhour
                    FROM tb_rp750
                    where checkdt between :frdate and :todate
                    UNION ALL
                    SELECT 'wm_hap_input' as  inspectypeCode,
                    checkdt, '합동안전점검' AS inspectype, chkaddres as inspecarea, '' as checkhour
                    FROM tb_rp720
                    where checkdt between :frdate and :todate
                    ORDER BY checkdt;
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
         return items;
    }
}
