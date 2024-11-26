package mes.app.operate.service;

import mes.domain.entity.actasEntity.*;
import mes.domain.repository.actasRepository.TB_RP880Repository;
import mes.domain.repository.actasRepository.TB_RP885Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ControlService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_RP880Repository TBRP880Repository;

    @Autowired
    TB_RP885Repository TBRP885Repository;


    // 저장
    @Transactional
    public Boolean save(TB_RP880 tbRp880) {

        try {
            TBRP880Repository.save(tbRp880);
            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    @Transactional
    public Boolean saveDetail(TB_RP885 tbRp885) {

        try {
            TBRP885Repository.save(tbRp885);
            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    // 검색
    public List<Map<String, Object>> getList(String startDate, String endDate, String spworkcd, String spcompcd, String spplancd) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("startDate", startDate);
        dicParam.addValue("endDate", endDate);
        dicParam.addValue("spworkcd", spworkcd);
        dicParam.addValue("spcompcd", spcompcd);
        dicParam.addValue("spplancd", spplancd);

        sql.append("""
                SELECT
                    ROW_NUMBER() OVER (ORDER BY t1.contdt DESC, t1.indatem DESC) AS rownum,
                    t1.*,
                    t2.contseq,
                    t2.contnum,
                    t2.conttime,
                    t2.contsequsr
                FROM
                    tb_rp880 t1
                LEFT JOIN
                    tb_rp885 t2
                ON
                    t1.checkdt = t2.checkdt AND
                    t1.contdt = t2.contdt AND
                    t1.spworkcd = t2.spworkcd AND
                    t1.spcompcd = t2.spcompcd AND
                    t1.spplancd = t2.spplancd
                WHERE
                    t1.spworkcd = :spworkcd AND
                    t1.spcompcd = :spcompcd AND
                    t1.spplancd = :spplancd
                """);

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND t1.contdt >= :startDate ");
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND t1.contdt <= :endDate ");
        }

        // 마지막으로 order by 절 추가
        sql.append("""
                GROUP BY
                    t1.spworkcd, t1.spcompcd, t1.spplancd, t1.checkdt, t1.contdt, t1.contstime, t1.contetime, t1.contdrive, t1.contusr, t1.contarea, t2.contnum, t2.contseq, t2.conttime, t2.contsequsr
                ORDER BY
                    t1.contdt DESC, t1.indatem DESC
                """);

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }

    // findById
    public Map<String, Object> findById(TB_RP880_PK pk) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("spworkcd", pk.getSpworkcd());
        dicParam.addValue("spcompcd", pk.getSpcompcd());
        dicParam.addValue("spplancd", pk.getSpplancd());
        dicParam.addValue("checkdt", pk.getCheckdt());
        dicParam.addValue("contdt", pk.getContdt());

        sql.append("""
                select 
                    *
                from tb_rp880
                where 
                    spworkcd = :spworkcd
                    and spcompcd = :spcompcd
                    and spplancd = :spplancd
                    and checkdt = :checkdt
                    and contdt = :contdt
                """);
        Map<String, Object> item = this.sqlRunner.getRow(sql.toString(), dicParam);
        return item;
    }

    @Transactional
    public Boolean delete(TB_RP880_PK pk) {

        try {
            // TB_RP880 삭제
            Optional<TB_RP880> tbRp880 = TBRP880Repository.findById(pk);
            tbRp880.ifPresent(tb_rp880 -> TBRP880Repository.delete(tb_rp880));

            // TB_RP885 찾기
            List<TB_RP885> tbRp885List = TBRP885Repository.findAllById_CheckdtAndId_Contdt(pk.getCheckdt(), pk.getContdt());

            TBRP885Repository.deleteAll(tbRp885List);

            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    @Transactional
    public List<String> getSuggestions(String query, String field) {

        return switch (field) {
            case "contusr" -> TBRP880Repository.findContusrByQuery(query);
            case "contarea" -> TBRP880Repository.findContareaByQuery(query);
            case "contdrive" -> TBRP880Repository.findContdriveByQuery(query);
            case "contsequsr" -> TBRP885Repository.findContsequsrByQuery(query);

            // 다른 필드에 대한 처리
            default -> new ArrayList<>();
        };
    }

    // 최신 데이터 가져오기
    public Map<String, Object> getFirst(String spworkcd, String spcompcd, String spplancd) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("spworkcd", spworkcd);
        dicParam.addValue("spcompcd", spcompcd);
        dicParam.addValue("spplancd", spplancd);

        sql.append("""
                SELECT
                    *
                FROM
                    tb_rp880
                WHERE
                    spworkcd = :spworkcd AND
                    spcompcd = :spcompcd AND
                    spplancd = :spplancd
                ORDER BY
                    indatem DESC
                LIMIT 1;
                """);

        Map<String, Object> tbRp880Result = this.sqlRunner.getRow(sql.toString(), dicParam);

        // 만약 결과가 없으면 바로 null 반환
        if (tbRp880Result == null || tbRp880Result.isEmpty()) {
            return null;
        }

        // 2. tb_rp885에서 최신 contsequsr를 가져오기 위해 checkdt, contdt 사용
        String checkdt = (String) tbRp880Result.get("checkdt");
        String contdt = (String) tbRp880Result.get("contdt");

        dicParam.addValue("checkdt", checkdt);
        dicParam.addValue("contdt", contdt);

        StringBuilder sql2 = new StringBuilder();
        sql2.append("""
            SELECT
                contsequsr
            FROM
                tb_rp885
            WHERE
                checkdt = :checkdt AND
                contdt = :contdt
            ORDER BY
                indatem DESC
            LIMIT 1;
            """);

        Map<String, Object> tbRp885Result = this.sqlRunner.getRow(sql2.toString(), dicParam);

        // tb_rp880 결과에 tb_rp885의 contsequsr 값을 추가
        if (tbRp885Result != null && !tbRp885Result.isEmpty()) {
            tbRp880Result.put("contsequsr", tbRp885Result.get("contsequsr"));
        }

        return tbRp880Result;



    }

}
