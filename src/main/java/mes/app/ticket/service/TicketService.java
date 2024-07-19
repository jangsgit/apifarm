package mes.app.ticket.service;

import mes.domain.entity.actasEntity.TB_RP820;
import mes.domain.entity.actasEntity.TB_RP920;
import mes.domain.repository.TB_RP820Repository;
import mes.domain.repository.TB_RP920Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_RP820Repository tb_rp820Repository;

    @Transactional
    public Boolean save(TB_RP820 tbRp820){

        try{
            tb_rp820Repository.save(tbRp820);

            return true;

        }catch (Exception e){
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    public List<Map<String, Object>> getList(String searchtketnm, String startDate, String endDate) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("searchtketnm", "%" +searchtketnm+ "%");
        dicParam.addValue("startDate", startDate);
        dicParam.addValue("endDate", endDate);

        sql.append("""
                select 
                ROW_NUMBER() OVER (ORDER BY tketcrdtm DESC) AS rownum,
                *
                from tb_rp820
                """);

        // 조건 추가
        boolean hasWhereClause = false;

        if (searchtketnm != null && !searchtketnm.isEmpty()) {
            sql.append(" where \"tketnm\" like :searchtketnm");
            hasWhereClause = true;
        }

        if (startDate != null && !startDate.isEmpty()) {
            if (!hasWhereClause) {
                sql.append(" where ");
                hasWhereClause = true;
            } else {
                sql.append(" and ");
            }
            sql.append(" \"tketcrdtm\" >= :startDate");
        }

        if (endDate != null && !endDate.isEmpty()) {
            if (!hasWhereClause) {
                sql.append(" where ");
                hasWhereClause = true;
            } else {
                sql.append(" and ");
            }
            sql.append(" \"tketcrdtm\" <= :endDate");
        }

        // 마지막으로 order by 절 추가
        sql.append(" order by tketcrdtm desc");

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);

        // tketflag 값 변환을 위한 맵핑
        Map<String, String> tketflagMapping = new HashMap<>();
        tketflagMapping.put("1", "중지");
        tketflagMapping.put("2", "가동중");

        // tketflag 값 변환
        for (Map<String, Object> item : items) {
            if (item.containsKey("tketflag")) {
                Object tketflagValue = item.get("tketflag");
                if (tketflagValue instanceof String) {
                    String mappedValue = tketflagMapping.get(tketflagValue);
                    if (mappedValue != null) {
                        item.put("tketflag", mappedValue);
                    }
                }
            }
        }

        return items;
    }
}
