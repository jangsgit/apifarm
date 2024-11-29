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
                       au.spjangcd
                FROM TB_XUSERS xs
                left join auth_user au on au."username" = xs.userid
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
        Map<String, Object> searchLabels_02 = Map.of(
                "param1", "GRACE",
                "param2", "ZZ",
                "param3", "20230101",
                "param4", "20241201",
                "param5", "00",
                "param6", "%",
                "param7", "%"
        );
        dicParam.addValue("param1", searchLabels_02.get("param1"));  // searchLabels.get("param1")
        dicParam.addValue("param2", searchLabels_02.get("param2"));
        dicParam.addValue("param3", searchLabels_02.get("param3"));
        dicParam.addValue("param4", searchLabels_02.get("param4"));
        dicParam.addValue("param5", searchLabels_02.get("param5"));
        dicParam.addValue("param6", searchLabels_02.get("param6"));
        dicParam.addValue("param7", searchLabels_02.get("param7"));

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

}
