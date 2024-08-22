package mes.app.actas_inspec.service;



import mes.domain.entity.actasEntity.*;
import mes.domain.repository.TB_RP870Repository;
import mes.domain.repository.TB_RP875Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.*;

@Service
public class DocService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_RP870Repository TBRP870Repository;

    @Autowired
    TB_RP875Repository TBRP875Repository;


    // 저장
    @Transactional
    public Boolean save(TB_RP870 tbRp870) {

        try {
            TBRP870Repository.save(tbRp870);
            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    @Transactional
    public Boolean saveFile(TB_RP875 tbRp875) {

        try {
            TBRP875Repository.save(tbRp875);
            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    // 검색
    public List<Map<String, Object>> getList(String searchTitle, String startDate, String endDate, Integer finddocdv, String spworkcd, String spcompcd, String spplancd) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("searchTitle", "%" + searchTitle + "%");
        dicParam.addValue("startDate", startDate);
        dicParam.addValue("endDate", endDate);
        dicParam.addValue("finddocdv", finddocdv);
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
                    tb_rp870 t1
                LEFT JOIN
                    tb_rp875 t2
                ON
                    t1.spworkcd = t2.spworkcd AND
                    t1.spcompcd = t2.spcompcd AND
                    t1.spplancd = t2.spplancd AND
                    t1.registdt = t2.registdt AND
                    t1.checkseq = t2.checkseq
                WHERE
                    t1.spworkcd = :spworkcd AND
                    t1.spcompcd = :spcompcd AND
                    t1.spplancd = :spplancd
                """);

        if (searchTitle != null && !searchTitle.isEmpty()) {
            sql.append(" AND t1.doctitle LIKE :searchTitle ");
        }

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND t1.registdt >= :startDate ");
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND t1.registdt <= :endDate ");
        }

        if (finddocdv != 0) {
            sql.append(" AND t1.docdv = :finddocdv ");
        }

        // 마지막으로 order by 절 추가
        sql.append("""
                GROUP BY
                    t1.spworkcd, t1.spcompcd, t1.spplancd, t1.registdt, t1.checkseq, t1.doctitle, t1.docdv
                ORDER BY
                    t1.registdt DESC, t1.indatem DESC
                """);

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }

    // File download
    public List<Map<String, Object>> download(TB_RP870_PK pk) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("spworkcd", pk.getSpworkcd());
        dicParam.addValue("spcompcd", pk.getSpcompcd());
        dicParam.addValue("spplancd", pk.getSpplancd());
        dicParam.addValue("registdt", pk.getRegistdt());
        dicParam.addValue("checkseq", pk.getCheckseq());

        sql.append("""
                select 
                    t1.doctitle,
                    t2.*
                from tb_rp870 t1
                join tb_rp875 t2
                on 
                    t1.spworkcd = t2.spworkcd 
                    AND t1.spcompcd = t2.spcompcd 
                    AND t1.spplancd = t2.spplancd 
                    AND t1.registdt = t2.registdt 
                    AND t1.checkseq = t2.checkseq
                where 
                    t2.spworkcd like :spworkcd
                    and t2.spcompcd like :spcompcd
                    and t2.spplancd like :spplancd
                    and t2.registdt like :registdt
                    and t2.checkseq like :checkseq
                """);
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }

    // delete
    @Transactional
    public Boolean delete(TB_RP870_PK pk) {

        try {
            // TB_RP870 삭제
            Optional<TB_RP870> tbRp870 = TBRP870Repository.findById(pk);
            tbRp870.ifPresent(tb_rp870 -> TBRP870Repository.delete(tb_rp870));

            // TB_RP875 찾기
            List<TB_RP875> tbRp875List = TBRP875Repository.findAllByIdSpworkcdAndIdSpcompcdAndIdSpplancdAndIdRegistdtAndIdCheckseq(
                    pk.getSpworkcd(), pk.getSpcompcd(), pk.getSpplancd(), pk.getRegistdt(), pk.getCheckseq());

            // 파일 삭제
            for (TB_RP875 tbRp875 : tbRp875List) {
                String filePath = tbRp875.getFilepath();
                String fileName = tbRp875.getFilesvnm();
                File file = new File(filePath, fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
            TBRP875Repository.deleteAll(tbRp875List);

            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    // findById
    public Map<String, Object> findById(TB_RP870_PK pk) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("spworkcd", pk.getSpworkcd());
        dicParam.addValue("spcompcd", pk.getSpcompcd());
        dicParam.addValue("spplancd", pk.getSpplancd());
        dicParam.addValue("registdt", pk.getRegistdt());
        dicParam.addValue("checkseq", pk.getCheckseq());

        sql.append("""
                select 
                    t1.*,
                    t2.spworkcd as t2_spworkcd,
                    t2.spcompcd as t2_spcompcd,
                    t2.spplancd as t2_spplancd,
                    t2.registdt as t2_registdt,
                    t2.checkseq as t2_checkseq,
                    t2.docseq as t2_docseq,
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
                from tb_rp870 t1
                left join tb_rp875 t2
                on 
                    t1.spworkcd = t2.spworkcd 
                    AND t1.spcompcd = t2.spcompcd 
                    AND t1.spplancd = t2.spplancd 
                    AND t1.registdt = t2.registdt 
                    AND t1.checkseq = t2.checkseq
                where 
                    t1.spworkcd like :spworkcd
                    and t1.spcompcd like :spcompcd
                    and t1.spplancd like :spplancd
                    and t1.registdt like :registdt
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
                fileData.put("registdt", row.get("t2_registdt"));
                fileData.put("checkseq", row.get("t2_checkseq"));
                fileData.put("docseq", row.get("t2_docseq"));
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
}
