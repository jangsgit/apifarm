package mes.app.mobile_production.service;

import mes.domain.entity.actasEntity.TB_DA006W_PK;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductionService {
    @Autowired
    SqlRunner sqlRunner;

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

}
