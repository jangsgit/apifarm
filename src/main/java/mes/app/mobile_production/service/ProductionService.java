package mes.app.mobile_production.service;

import mes.domain.entity.actasEntity.TB_DA006W_PK;
import mes.domain.model.AjaxResult;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductionService {
    @Autowired
    SqlRunner sqlRunner;

    // username으로 cltcd, cltnm, saupnum, custcd 가지고 오기
    public Map<String, Object> getUserInfo(String username) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql = """
                select xs.custcd,
                       xs.spjangcd
                FROM TB_XUSERS xs
                WHERE xs.userid = :username
                """;
        dicParam.addValue("username", username);
        Map<String, Object> userInfo = this.sqlRunner.getRow(sql, dicParam);
        return userInfo;
    }

    //카드리스트 불러오기
    public List<Map<String, Object>> getProductionList(Map<String, Object> searchLabels) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "EXEC SP_FPLAN_VIEW :param1, :param2, :param3, :param4, :param5, :param6, :param7";

        dicParam.addValue("param1", searchLabels.get("search_custcd"));  // searchLabels.get("param1")
        dicParam.addValue("param2", searchLabels.get("search_spjangcd"));
        dicParam.addValue("param3", searchLabels.get("search_startDate"));
        dicParam.addValue("param4", searchLabels.get("search_endDate"));
        dicParam.addValue("param5", "00");
        if(!searchLabels.get("search_cltcd").toString().isEmpty()) {
            dicParam.addValue("param6", searchLabels.get("search_cltcd"));
        }else {
            dicParam.addValue("param6", "%");
        }
        if(!searchLabels.get("search_product").toString().isEmpty()) {
            dicParam.addValue("param7", searchLabels.get("search_product"));
        }else {
            dicParam.addValue("param7", "%");
        }

        try {
            items = this.sqlRunner.getRows(sql, dicParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items != null ? items : List.of();
    }

    //작지리스트 불러오기
    public List<Map<String, Object>> getWorkList(Map<String, Object> searchLabels) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "  SELECT CONVERT(CHAR(8), TB_FPLAN_WORK.wtrdt, 112) AS wtrdt,   \n" +
                "       TB_FPLAN.plan_no,   \n" +
                "       TB_FPLAN.wono,   \n" +
                "       TB_FPLAN.pcode,   \n" +
                "       TB_CA501.phm_pnam AS pname,   \n" +
                "       TB_CA501.phm_size AS psize,   \n" +
                "       TB_CA501.phm_unit AS punit,   \n" +
                "       TB_FPLAN.cltcd,\n" +
                "       TB_FPLAN_WORK.wflag AS wflag,\n" +
                "       TB_FPLAN_WORK.wotqt AS wotqt,   \n" +
                "       TB_FPLAN_WORK.wbdqt AS wbdqt,   \n" +
                "       TB_FPLAN_WORK.wotqt - TB_FPLAN_WORK.wbdqt AS wokqt,   \n" +
                "       TB_FPLAN_WORK.wremark AS remark\n" +
                "FROM TB_FPLAN WITH(NOLOCK)\n" +
                "INNER JOIN TB_FPLAN_WORK WITH(NOLOCK) ON \n" +
                "       TB_FPLAN.custcd = TB_FPLAN_WORK.custcd AND\n" +
                "       TB_FPLAN.spjangcd = TB_FPLAN_WORK.spjangcd AND\n" +
                "       TB_FPLAN.plan_no = TB_FPLAN_WORK.plan_no\n" +
                "INNER JOIN TB_CA501 WITH(NOLOCK) ON \n" +
                "       TB_FPLAN.custcd = TB_CA501.phm_cust AND\n" +
                "       TB_FPLAN.pcode = TB_CA501.phm_pcod\n" +
                "WHERE TB_FPLAN.custcd = :custcd\n" +
                "  AND TB_FPLAN.spjangcd = :spjangcd\n" +
                "  AND TB_FPLAN.wono = :wono\n" +
                "  AND TB_FPLAN_WORK.decision = '0';";

        dicParam.addValue("custcd", searchLabels.get("search_custcd"));
        dicParam.addValue("spjangcd", searchLabels.get("search_spjangcd"));
        dicParam.addValue("wono", searchLabels.get("wono"));

        try {
            items = this.sqlRunner.getRows(sql, dicParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items != null ? items : List.of();
    }

    //거래처 검색
    public List<Map<String, Object>> searchCltcd (String searchCltnm) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT cltcd,   " + // 거래처 코드
                "       cltnm     " +   // 거래처명
                "  FROM TB_XCLIENT WITH(NOLOCK)" +
                " WHERE (cltcd  LIKE :searchCltcd" +
                "    OR  cltnm  LIKE :searchCltcd)";

        if(!searchCltnm.isEmpty()) {
            dicParam.addValue("searchCltcd", "%" + searchCltnm + "%");  // searchLabels.get("param1")
        }else {
            dicParam.addValue("searchCltcd", "%");
        }
        try {
            items = this.sqlRunner.getRows(sql, dicParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items != null ? items : List.of();
    }

    // 품목 검색
    public List<Map<String, Object>> searchProduct(String searchProduct) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT   TB_CA501.phm_pcod," + // 제품코드
                "       TB_CA501.phm_pnam, " + // 품명
                "       TB_CA501.phm_size   FROM TB_CA501 WITH(NOLOCK)" + // 규격
                " WHERE (TB_CA501.phm_pcod LIKE :searchProduct " +
                "    OR  TB_CA501.phm_pnam LIKE :searchProduct " +
                "    OR  TB_CA501.phm_size LIKE :searchProduct ) " +
                "   AND (TB_CA501.useyn    = '1')";

        if(!searchProduct.isEmpty()) {
            dicParam.addValue("searchProduct","%" + searchProduct + "%");
        }else {
            dicParam.addValue("searchProduct", "%");
        }


        try {
            items = this.sqlRunner.getRows(sql, dicParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items != null ? items : List.of();
    }

    // wflag 매핑
    public Map<String, Object> getProcess(String comCode) {
        Map<String, Object> item = new HashMap<>();
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql = "select com_cnam from TB_CA510 where com_cls = '040' and com_code<>'00' AND com_code = :com_code";

        dicParam.addValue("com_code", comCode);
        try {
            item = this.sqlRunner.getRow(sql, dicParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public List<Map<String, Object>> searchTodayGrid(Map<String, Object> searchLabels){
        List<Map<String, Object>> items = new ArrayList<>();
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql = "SELECT TB_FPLAN.cltcd,   \n" +
                "       DBO.DF_NM_RTN('Tb_XCLIENT', TB_FPLAN.custcd, TB_FPLAN.cltcd, '', '') cltnm,  \n" +
                "       TB_FPLAN.pcode,   \n" +
                "       DBO.DF_NM_RTN('Tb_CA501', TB_FPLAN.pcode, '', '', '') pname,  \n" +
                "       TB_FPLAN.prod_qty,   \n" +
                "       TB_FPLAN.end_qty,   \n" +
                "       CASE TB_FPLAN.cls_flag WHEN '1' THEN '미진행' WHEN '2' THEN '진행' WHEN '3' THEN '진행' WHEN '4' THEN '완료' END cls_flag\n" +
                "   FROM {oj TB_FPLAN WITH(NOLOCK) LEFT OUTER JOIN TB_FPLAN_WORK WITH(NOLOCK) \n" +
                "        ON TB_FPLAN.custcd = TB_FPLAN_WORK.custcd \n" +
                "        AND TB_FPLAN.spjangcd = TB_FPLAN_WORK.spjangcd \n" +
                "        AND TB_FPLAN.plan_no = TB_FPLAN_WORK.plan_no}  \n" +
                "  WHERE (TB_FPLAN.custcd     = :custcd)\n" +
                "    AND (TB_FPLAN.spjangcd   = :spjangcd)\n" +
                "    AND (TB_FPLAN_WORK.wstdt = :today\n" +
                "     OR  TB_FPLAN_WORK.wendt = :today\n" +
                "     OR (TB_FPLAN.prod_sdate = :today AND TB_FPLAN.cls_flag = '1'))\n" +
                "    AND (TB_FPLAN.cls_flag   NOT IN ('0', '9'))\n" +
                "  ORDER BY 1, 3\n";
        dicParam.addValue("custcd", searchLabels.get("search_custcd"));
        dicParam.addValue("spjangcd", searchLabels.get("search_spjangcd"));
        dicParam.addValue("today", searchLabels.get("search_todayDate"));

        try {
            items = this.sqlRunner.getRows(sql, dicParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

}
