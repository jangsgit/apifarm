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

        dicParam.addValue("param1", "GRACE");
        dicParam.addValue("param2", "ZZ");
        dicParam.addValue("param3", java.sql.Date.valueOf("2023-01-01"));
        dicParam.addValue("param4", java.sql.Date.valueOf("2024-12-31"));
        dicParam.addValue("param5", "00");
        dicParam.addValue("param6", "C00765");
        dicParam.addValue("param7", "", Types.VARCHAR);

        try {
            items = this.sqlRunner.getRows(sql, dicParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

}
