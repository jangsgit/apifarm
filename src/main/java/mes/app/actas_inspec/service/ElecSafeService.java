package mes.app.actas_inspec.service;

import mes.domain.entity.actasEntity.TB_RP750;
import mes.domain.entity.actasEntity.TB_RP750_PK;
import mes.domain.repository.TB_RP750Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ElecSafeService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_RP750Repository TBRP750Repository;

    // 저장
    @Transactional
    public Boolean save(TB_RP750 tbRp750){

        try{
            TBRP750Repository.save(tbRp750);

            return true;

        }catch (Exception e){
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    // 검색
    public List<Map<String, Object>> getList(String searchTitle, String startDate, String endDate) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("searchTitle", "%" +searchTitle+ "%");
        dicParam.addValue("startDate", startDate);
        dicParam.addValue("endDate", endDate);

        sql.append("""
                select 
                ROW_NUMBER() OVER (ORDER BY registdt DESC) AS rownum,
                *
                from tb_rp750
                """);

        // 조건 추가
        boolean hasWhereClause = false;

        if (searchTitle != null && !searchTitle.isEmpty()) {
            sql.append(" where \"title\" like :searchTitle");
            hasWhereClause = true;
        }

        if (startDate != null && !startDate.isEmpty()) {
            if (!hasWhereClause) {
                sql.append(" where ");
                hasWhereClause = true;
            } else {
                sql.append(" and ");
            }
            sql.append(" \"registdt\" >= :startDate");
        }

        if (endDate != null && !endDate.isEmpty()) {
            if (!hasWhereClause) {
                sql.append(" where ");
                hasWhereClause = true;
            } else {
                sql.append(" and ");
            }
            sql.append(" \"registdt\" <= :endDate");
        }

        // 마지막으로 order by 절 추가
        sql.append(" order by registdt desc");

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }

    // delete
    @Transactional
    public Boolean delete(TB_RP750 tbRp750){

        try{
            TBRP750Repository.delete(tbRp750);

            return true;

        }catch (Exception e){
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    // findById
    public Optional<TB_RP750> findById(TB_RP750_PK pk) {
        return TBRP750Repository.findById(pk);
    }
}
