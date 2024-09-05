package mes.app.actas_inspec.service;

import mes.config.Settings;
import mes.domain.entity.actasEntity.*;
import mes.domain.repository.TB_RP720Repository;
import mes.domain.repository.TB_RP725Repository;
import mes.domain.repository.TB_RP726Repository;
import mes.domain.repository.TB_RP750Repository;
import mes.domain.repository.actasRepository.TB_INSPECRepository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.*;

@Service
public class HapReportService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_RP720Repository TBRP720Repository;

    @Autowired
    TB_RP725Repository TBRP725Repository;

    @Autowired
    TB_RP726Repository TBRP726Repository;

    @Autowired
    TB_INSPECRepository tb_inspecRepository;

    @Transactional
    public Boolean save(TB_RP720 tbRp720) {

        try {
            TBRP720Repository.save(tbRp720);
            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    @Transactional
    public Boolean saveFile(TB_RP725 tbRp725) {

        try {
            TBRP725Repository.save(tbRp725);
            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    public List<Map<String, Object>> getList(String searchusr, String startDate, String endDate, String spworkcd, String spcompcd, String spplancd, String searchcom) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("searchusr", "%" + searchusr + "%");
        dicParam.addValue("searchcom", "%" + searchcom + "%");
        dicParam.addValue("startDate", startDate);
        dicParam.addValue("endDate", endDate);
        dicParam.addValue("spworkcd", spworkcd);
        dicParam.addValue("spcompcd", spcompcd);
        dicParam.addValue("spplancd", spplancd);

        // 정수로 변환한 값 추가
        Integer spworkcdInt = spworkcd != null ? Integer.parseInt(spworkcd) : null;
        Integer spcompcdInt = spcompcd != null ? Integer.parseInt(spcompcd) : null;
        Integer spplancdInt = spplancd != null ? Integer.parseInt(spplancd) : null;

        dicParam.addValue("spworkcdInt", spworkcdInt);
        dicParam.addValue("spcompcdInt", spcompcdInt);
        dicParam.addValue("spplancdInt", spplancdInt);

        sql.append("""
                SELECT
                    ROW_NUMBER() OVER (ORDER BY t1.checkdt DESC, t1.indatem DESC) AS rownum,
                    t1.spworkcd as spworkcd,
                    t1.spcompcd as spcompcd,
                    t1.spplancd as spplancd,
                    t1.checkdt AS checkdt,
                    t1.checkno AS checkno,
                    t1.checknm as checknm,
                    t1.checkrem as checkrem,
                    t1.chkaddres as chkaddres,
                    uc1."Value" AS spworknm,
                    uc2."Value" AS spcompnm,
                    uc3."Value" AS spplannm,
                    STRING_AGG(CAST(t2.fileornm AS TEXT), ',') AS filenames,
                    STRING_AGG(CAST(t2.filepath AS TEXT), ',') AS filepaths,
                    STRING_AGG(CAST(t3.checkusr AS TEXT), ',') AS checkusrs
                FROM
                    tb_rp720 t1
                LEFT JOIN
                    tb_rp725 t2 ON
                    t1.spworkcd = t2.spworkcd AND
                    t1.spcompcd = t2.spcompcd AND
                    t1.spplancd = t2.spplancd AND
                    t1.checkdt = t2.checkdt AND
                    t1.checkno = t2.checkno
                LEFT JOIN
                    tb_rp726 t3 ON
                    t1.spworkcd = t3.spworkcd AND
                    t1.spcompcd = t3.spcompcd AND
                    t1.spplancd = t3.spplancd AND
                    t1.checkdt = t3.checkdt AND
                    t1.checkno = t3.checkno
                LEFT JOIN
                    user_code uc1 ON uc1.id = :spworkcdInt
                LEFT JOIN
                    user_code uc2 ON uc2.id = :spcompcdInt
                LEFT JOIN
                    user_code uc3 ON uc3.id = :spplancdInt
                WHERE
                    t1.spworkcd = :spworkcd AND
                    t1.spcompcd = :spcompcd AND
                    t1.spplancd = :spplancd
                """);

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND t1.checkdt >= :startDate ");
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND t1.checkdt <= :endDate ");
        }

        // 마지막으로 order by 절 추가
        sql.append("""
                GROUP BY
                    t1.spworkcd, t1.spcompcd, t1.spplancd, t1.checkdt, t1.checkno, t1.checknm, uc1."Value", uc2."Value", uc3."Value"
                HAVING
                    (:searchusr IS NULL OR STRING_AGG(CAST(t3.checkusr AS TEXT), ',') LIKE :searchusr)
                    AND (:searchcom IS NULL OR STRING_AGG(CAST(t3.company AS TEXT), ',') LIKE :searchcom)
                ORDER BY
                    t1.checkdt DESC, t1.indatem DESC
                """);

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }

    // findById
    public Map<String, Object> findById(TB_RP720_PK pk) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        dicParam.addValue("spworkcd", pk.getSpworkcd());
        dicParam.addValue("spcompcd", pk.getSpcompcd());
        dicParam.addValue("spplancd", pk.getSpplancd());
        dicParam.addValue("checkdt", pk.getCheckdt());
        dicParam.addValue("checkno", pk.getCheckno());

        Map<String, Object> result = new HashMap<>();

        String sql1 = """
        select *
        from tb_rp720
        where spworkcd like :spworkcd
        and spcompcd like :spcompcd
        and spplancd like :spplancd
        and checkdt like :checkdt
        and checkno like :checkno
        """;
        Map<String, Object> tbrp720 = this.sqlRunner.getRow(sql1, dicParam);

        if (tbrp720 != null) {
            result.putAll(tbrp720);
        }

        // 점검사항
        String sql2 = """
        select *
        from tb_inspec
        where spworkcd like :spworkcd
        and spcompcd like :spcompcd
        and spplancd like :spplancd
        and checkdt like :checkdt
        and checkno like :checkno
        """;
        List<Map<String, Object>> inspectionItems = this.sqlRunner.getRows(sql2, dicParam);

        result.put("inspectionItems", inspectionItems);

        // 파일 리스트
        String sql3 = """
        select *
        from tb_rp725
        where spworkcd like :spworkcd
        and spcompcd like :spcompcd
        and spplancd like :spplancd
        and checkdt like :checkdt
        and checkno like :checkno
        """;

        List<Map<String, Object>> filelist = this.sqlRunner.getRows(sql3, dicParam);
        result.put("filelist", filelist);

        // 점검자 리스트
        String sql4 = """
        select *
        from tb_rp726
        where spworkcd like :spworkcd
        and spcompcd like :spcompcd
        and spplancd like :spplancd
        and checkdt like :checkdt
        and checkno like :checkno
        order by seq
        """;

        List<Map<String, Object>> inspectorlist = this.sqlRunner.getRows(sql4, dicParam);
        result.put("inspectorlist", inspectorlist);

        return result;
    }

    // delete
    @Transactional
    public Boolean delete(TB_RP720_PK pk) {

        try {
            // TB_RP720 삭제
            Optional<TB_RP720> tbRp720 = TBRP720Repository.findById(pk);
            tbRp720.ifPresent(tb_rp720 -> TBRP720Repository.delete(tb_rp720));

            // TB_INSPEC 삭제
            List<TB_INSPEC> tbInspecList = tb_inspecRepository.findAllBySpworkcdAndSpcompcdAndSpplancdAndCheckdtAndCheckno(
                    pk.getSpworkcd(), pk.getSpcompcd(), pk.getSpplancd(), pk.getCheckdt(), pk.getCheckno());
            tb_inspecRepository.deleteAll(tbInspecList);

            // TB_RP725 찾기
            List<TB_RP725> tbRp725List = TBRP725Repository.findAllByIdSpworkcdAndIdSpcompcdAndIdSpplancdAndIdCheckdtAndIdCheckno(
                    pk.getSpworkcd(), pk.getSpcompcd(), pk.getSpplancd(), pk.getCheckdt(), pk.getCheckno());

            // 파일 삭제
            for (TB_RP725 tbRp725 : tbRp725List) {
                String filePath = tbRp725.getFilepath();
                String fileName = tbRp725.getFilesvnm();
                File file = new File(filePath, fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
            TBRP725Repository.deleteAll(tbRp725List);

            // TB_RP726 삭제
            List<TB_RP726> tbRp726List = TBRP726Repository.findAllBySpworkcdAndSpcompcdAndSpplancdAndCheckdtAndCheckno(
                    pk.getSpworkcd(), pk.getSpcompcd(), pk.getSpplancd(), pk.getCheckdt(), pk.getCheckno());
            TBRP726Repository.deleteAll(tbRp726List);

            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    // File download
    public List<Map<String, Object>> download(TB_RP720_PK pk) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("spworkcd", pk.getSpworkcd());
        dicParam.addValue("spcompcd", pk.getSpcompcd());
        dicParam.addValue("spplancd", pk.getSpplancd());
        dicParam.addValue("checkdt", pk.getCheckdt());
        dicParam.addValue("checkno", pk.getCheckno());

        sql.append("""
                select 
                    t1.checknm,
                    t2.*
                from tb_rp720 t1
                join tb_rp725 t2
                on 
                    t1.spworkcd = t2.spworkcd 
                    AND t1.spcompcd = t2.spcompcd 
                    AND t1.spplancd = t2.spplancd 
                    AND t1.checkdt = t2.checkdt 
                    AND t1.checkno = t2.checkno
                where 
                    t2.spworkcd like :spworkcd
                    and t2.spcompcd like :spcompcd
                    and t2.spplancd like :spplancd
                    and t2.checkdt like :checkdt
                    and t2.checkno like :checkno
                """);
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }

    @Transactional
    public List<String> getSuggestions(String query, String field) {
        String table = "tbRp720";
        // 필드가 "checkusr"로 시작하는지 확인
        if (field.startsWith("checkusr")) {
            String[] parts = field.split("_");  // "checkusr1_company_1"을 "_로 분할"
            if (parts.length >= 3) {
                String checkusrType = parts[0]; // checkusr1, checkusr2
                String fieldType = parts[1];    // company, position, name
                String tableSuffix = parts[2];  // 1, 2, 3 (필드 구분용)

                if (checkusrType.equals("checkusr1")) {
                    return switch (fieldType) {
                        case "company" -> TBRP726Repository.findCompany_doByQuery(query, table);
                        case "position" -> TBRP726Repository.findPosition_doByQuery(query, table);
                        case "name" -> TBRP726Repository.findName_doByQuery(query, table);
                        default -> new ArrayList<>();
                    };
                } else if (checkusrType.equals("checkusr2")) {
                    return switch (fieldType) {
                        case "company" -> TBRP726Repository.findCompany_haByQuery(query, table);
                        case "position" -> TBRP726Repository.findPosition_haByQuery(query, table);
                        case "name" -> TBRP726Repository.findName_haByQuery(query, table);
                        default -> new ArrayList<>();
                    };
                }
            }
        }

        // 고정 필드에 대한 처리
        return switch (field) {
            case "chkaddres" -> TBRP720Repository.findChkaddresByQuery(query);
            case "checknm" -> TBRP720Repository.findChecknmByQuery(query);
            default -> new ArrayList<>();
        };

    }
}
