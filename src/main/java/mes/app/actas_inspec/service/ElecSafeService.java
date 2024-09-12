package mes.app.actas_inspec.service;

import mes.domain.entity.actasEntity.TB_RP750;
import mes.domain.entity.actasEntity.TB_RP750_PK;
import mes.domain.entity.actasEntity.TB_RP760;
import mes.domain.entity.actasEntity.TB_RP760_PK;
import mes.domain.repository.TB_RP750Repository;
import mes.domain.repository.TB_RP760Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.*;

@Service
public class ElecSafeService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_RP750Repository TBRP750Repository;

    @Autowired
    TB_RP760Repository TBRP760Repository;


    // 저장
    @Transactional
    public Boolean save(TB_RP750 tbRp750) {

        try {
            TBRP750Repository.save(tbRp750);
            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    @Transactional
    public Boolean saveFile(TB_RP760 tbRp760) {

        try {
            TBRP760Repository.save(tbRp760);
            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    // 검색
    public List<Map<String, Object>> getList(String searchTitle, String startDate, String endDate, String spworkcd, String spcompcd, String spplancd) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("searchTitle", "%" + searchTitle + "%");
        dicParam.addValue("startDate", startDate);
        dicParam.addValue("endDate", endDate);
        dicParam.addValue("spworkcd", spworkcd);
        dicParam.addValue("spcompcd", spcompcd);
        dicParam.addValue("spplancd", spplancd);

        sql.append("""
                SELECT
                    ROW_NUMBER() OVER (ORDER BY t1.registdt DESC, t1.indatem DESC) AS rownum,
                    t1.*,
                    STRING_AGG(CAST(t2.fileornm AS TEXT), ',') AS filenames,
                    STRING_AGG(CAST(t2.filepath AS TEXT), ',') AS filepaths
                FROM
                    tb_rp750 t1
                LEFT JOIN
                    tb_rp760 t2
                ON
                    t1.spworkcd = t2.spworkcd AND
                    t1.spcompcd = t2.spcompcd AND
                    t1.spplancd = t2.spplancd AND
                    t1.checkdt = t2.checkdt AND
                    t1.checkseq = t2.checkseq
                WHERE
                    t1.spworkcd = :spworkcd AND
                    t1.spcompcd = :spcompcd AND
                    t1.spplancd = :spplancd
                """);

        if (searchTitle != null && !searchTitle.isEmpty()) {
            sql.append(" AND t1.checktitle LIKE :searchTitle ");
        }

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND t1.registdt >= :startDate ");
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND t1.registdt <= :endDate ");
        }

        // 마지막으로 order by 절 추가
        sql.append("""
                GROUP BY
                    t1.spworkcd, t1.spcompcd, t1.spplancd, t1.checkdt, t1.checkseq, t1.registdt, t1.checktitle, t1.endresult
                ORDER BY
                    t1.registdt DESC, t1.indatem DESC
                """);

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }

    // delete
    @Transactional
    public Boolean delete(TB_RP750_PK pk) {

        try {
            // TB_RP750 삭제
            Optional<TB_RP750> tbRp750 = TBRP750Repository.findById(pk);
            tbRp750.ifPresent(tb_rp750 -> TBRP750Repository.delete(tb_rp750));

            // TB_RP760 찾기
            List<TB_RP760> tbRp760List = TBRP760Repository.findAllByIdSpworkcdAndIdSpcompcdAndIdSpplancdAndIdCheckdtAndIdCheckseq(
                    pk.getSpworkcd(), pk.getSpcompcd(), pk.getSpplancd(), pk.getCheckdt(), pk.getCheckseq());

            // 파일 삭제
            for (TB_RP760 tbRp760 : tbRp760List) {
                String filePath = tbRp760.getFilepath();
                String fileName = tbRp760.getFilesvnm();
                File file = new File(filePath, fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
            TBRP760Repository.deleteAll(tbRp760List);

            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    // findById
    public Map<String, Object> findById(TB_RP750_PK pk) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("spworkcd", pk.getSpworkcd());
        dicParam.addValue("spcompcd", pk.getSpcompcd());
        dicParam.addValue("spplancd", pk.getSpplancd());
        dicParam.addValue("checkdt", pk.getCheckdt());
        dicParam.addValue("checkseq", pk.getCheckseq());

        sql.append("""
                select 
                    t1.*,
                    t2.spworkcd as t2_spworkcd,
                    t2.spcompcd as t2_spcompcd,
                    t2.spplancd as t2_spplancd,
                    t2.checkdt as t2_checkdt,
                    t2.checkseq as t2_checkseq,
                    t2.fileseq as t2_fileseq,
                    t2.filepath,
                    t2.filesvnm,
                    t2.fileextns,
                    t2.fileurl,
                    t2.fileornm,
                    t2.filesize,
                    t2.filerem,
                    t2.repyn,
                    t2.indatem,
                    t2.inuserid,
                    t2.inusernm
                from tb_rp750 t1
                left join tb_rp760 t2
                on 
                    t1.spworkcd = t2.spworkcd 
                    AND t1.spcompcd = t2.spcompcd 
                    AND t1.spplancd = t2.spplancd 
                    AND t1.checkdt = t2.checkdt 
                    AND t1.checkseq = t2.checkseq
                where 
                    t1.spworkcd like :spworkcd
                    and t1.spcompcd like :spcompcd
                    and t1.spplancd like :spplancd
                    and t1.checkdt like :checkdt
                    and t1.checkseq like :checkseq
                """);
        List<Map<String, Object>> rows = this.sqlRunner.getRows(sql.toString(), dicParam);

        if (rows.isEmpty()) {
            return Collections.emptyMap();
        }
        // 기본 정보는 첫 번째 행에서 가져옵니다.
        Map<String, Object> result = new HashMap<>(rows.get(0));
        List<Map<String, Object>> filelist = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            if (row.get("t2_spworkcd") != null) {
                Map<String, Object> fileData = new HashMap<>();
                fileData.put("spworkcd", row.get("t2_spworkcd"));
                fileData.put("spcompcd", row.get("t2_spcompcd"));
                fileData.put("spplancd", row.get("t2_spplancd"));
                fileData.put("checkdt", row.get("t2_checkdt"));
                fileData.put("checkseq", row.get("t2_checkseq"));
                fileData.put("fileseq", row.get("t2_fileseq"));
                fileData.put("filepath", row.get("filepath"));
                fileData.put("filesvnm", row.get("filesvnm"));
                fileData.put("fileextns", row.get("fileextns"));
                fileData.put("fileurl", row.get("fileurl"));
                fileData.put("fileornm", row.get("fileornm"));
                fileData.put("filesize", row.get("filesize"));
                fileData.put("filerem", row.get("filerem"));
                fileData.put("repyn", row.get("repyn"));
                fileData.put("indatem", row.get("indatem"));
                fileData.put("inuserid", row.get("inuserid"));
                fileData.put("inusernm", row.get("inusernm"));
                filelist.add(fileData);
            }
        }

        result.put("filelist", filelist);
        return result;
    }

    // File download
    public List<Map<String, Object>> download(TB_RP750_PK pk) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("spworkcd", pk.getSpworkcd());
        dicParam.addValue("spcompcd", pk.getSpcompcd());
        dicParam.addValue("spplancd", pk.getSpplancd());
        dicParam.addValue("checkdt", pk.getCheckdt());
        dicParam.addValue("checkseq", pk.getCheckseq());

        sql.append("""
                select 
                    t1.checktitle,
                    t2.*
                from tb_rp750 t1
                join tb_rp760 t2
                on 
                    t1.spworkcd = t2.spworkcd 
                    AND t1.spcompcd = t2.spcompcd 
                    AND t1.spplancd = t2.spplancd 
                    AND t1.checkdt = t2.checkdt 
                    AND t1.checkseq = t2.checkseq
                where 
                    t2.spworkcd like :spworkcd
                    and t2.spcompcd like :spcompcd
                    and t2.spplancd like :spplancd
                    and t2.checkdt like :checkdt
                    and t2.checkseq like :checkseq
                """);
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }

    @Transactional
    public List<String> getSuggestions(String query, String field) {

        return switch (field) {
            case "checktitle" -> TBRP750Repository.findChecktitlesByQuery(query);
            case "checkusr" -> TBRP750Repository.findCheckusersByQuery(query);
            case "checkarea" -> TBRP750Repository.findCheckareasByQuery(query);

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
                    tb_rp750
                WHERE
                    spworkcd = :spworkcd AND
                    spcompcd = :spcompcd AND
                    spplancd = :spplancd
                ORDER BY
                    indatem DESC
                LIMIT 1;
                """);

        return this.sqlRunner.getRow(sql.toString(), dicParam);
    }

}
