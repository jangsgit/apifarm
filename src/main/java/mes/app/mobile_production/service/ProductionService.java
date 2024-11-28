package mes.app.mobile_production.service;

import mes.domain.entity.actasEntity.TB_DA006W_PK;
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

    //카드리스트 불러오기
    public List<Map<String, Object>> getProductionList(Map<String, Object> searchLabels) {
        Map<String, Object> dicParam = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "{ CALL SP_FPLAN_VIEW(?, ?, ?, ?, ?, ?, ?) }";
                //"EXEC SP_FPLAN_VIEW :param1, :param2, :param3, :param4, :param5, :param6, :param7";

        dicParam.put("param1", "GRACE");
        dicParam.put("param2", "ZZ");
        dicParam.put("param3", "20230101");
        dicParam.put("param4", "20241201");
        dicParam.put("param5", "00");
        dicParam.put("param6", "%");
        dicParam.put("param7", "%");

        try {
            items = sqlRunner.selectList(sql, dicParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

}
