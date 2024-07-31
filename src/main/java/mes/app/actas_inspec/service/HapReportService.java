package mes.app.actas_inspec.service;

import mes.config.Settings;
import mes.domain.entity.actasEntity.*;
import mes.domain.repository.TB_RP720Repository;
import mes.domain.repository.TB_RP725Repository;
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
    TB_INSPECRepository tb_inspecRepository;

    @Transactional
    public Boolean save(TB_RP720 tbRp720, TB_RP720_PK pk, List<String> doc_list){

        try{
            TBRP720Repository.save(tbRp720);

            List<String> divisionList = new ArrayList<>();
            List<String> contList = new ArrayList<>();
            List<String> resultList = new ArrayList<>();
            List<String> reformList = new ArrayList<>();
            List<Integer> seqList = new ArrayList<>();

            for (String doc : doc_list) {
                String[] parts = doc.split("@", -1);

                if (parts.length >= 5) {
                    Integer seq = parts[0].trim().isEmpty() ? null : Integer.parseInt(parts[0].trim());
                    seqList.add(seq);
                    divisionList.add(parts[1].trim());
                    contList.add(parts[2].trim());
                    resultList.add(parts[3].trim());
                    reformList.add(parts[4].trim());
                }
            }

            for (int i = 0; i < doc_list.size(); i++) {
                TB_INSPEC tb_inspec;

                if (seqList.get(i) != null) {
                    // seq 값이 존재하는 경우 기존 객체를 가져와 업데이트
                    Optional<TB_INSPEC> existingInspec = tb_inspecRepository.findBySeq(seqList.get(i));
                    if (existingInspec.isPresent()) {
                        tb_inspec = existingInspec.get();
                    } else {
                        // seq 값이 존재하지만 해당 seq에 해당하는 데이터가 없을 경우 새로 생성
                        tb_inspec = new TB_INSPEC();
                        tb_inspec.setSeq(seqList.get(i));
                    }
                } else {
                    // seq 값이 존재하지 않는 경우 새로 생성
                    int maxSeq;
                    Optional<Integer> seqValue = tb_inspecRepository.findTopByOrderBySeqDesc();
                    maxSeq = seqValue.orElse(0);  // MaxSeq를 0으로 초기화
                    tb_inspec = new TB_INSPEC();
                    tb_inspec.setSeq(maxSeq + 1);
                }

                tb_inspec.setSpworkcd(pk.getSpworkcd());
                tb_inspec.setSpworknm(tbRp720.getSpworknm());
                tb_inspec.setSpcompcd(pk.getSpcompcd());
                tb_inspec.setSpcompnm(tbRp720.getSpcompnm());
                tb_inspec.setSpplancd(pk.getSpplancd());
                tb_inspec.setSpplannm(tbRp720.getSpplannm());
                tb_inspec.setTabletype("TB_RP720");
                tb_inspec.setInspecnum(i + 1);
                tb_inspec.setInspecdivision(divisionList.get(i));
                tb_inspec.setInspeccont(contList.get(i));
                tb_inspec.setInspecresult(resultList.get(i));
                tb_inspec.setInspecreform(reformList.get(i));
                tb_inspec.setCheckdt(pk.getCheckdt());
                tb_inspec.setCheckno(pk.getCheckno());
                tb_inspecRepository.save(tb_inspec);
            }
            return true;

        }catch (Exception e){
            System.out.println(e + ": 에러발생");
            return false;
        }
    }


    public List<Map<String, Object>> getList(String searchusr, String startDate, String endDate) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        dicParam.addValue("paramusr", "%" +searchusr+ "%");

        String sql = """
                select 
                ROW_NUMBER() OVER (ORDER BY checkdt DESC) AS rownum,
                *
                from tb_rp720
                order by checkdt desc
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }

    // findById
    public Map<String, Object> findById(TB_RP720_PK pk) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("spworkcd", pk.getSpworkcd());
        dicParam.addValue("spcompcd", pk.getSpcompcd());
        dicParam.addValue("spplancd", pk.getSpplancd());
        dicParam.addValue("checkdt", pk.getCheckdt());
        dicParam.addValue("checkno", pk.getCheckno());

        sql.append("""
                select 
                    t1.*,
                    t2.*
                from tb_rp720 t1
                left join tb_inspec t2
                on 
                    t1.spworkcd = t2.spworkcd 
                    AND t1.spcompcd = t2.spcompcd 
                    AND t1.spplancd = t2.spplancd 
                    AND t1.checkdt = t2.checkdt 
                    AND t1.checkno = t2.checkno
                where 
                    t1.spworkcd like :spworkcd
                    and t1.spcompcd like :spcompcd
                    and t1.spplancd like :spplancd
                    and t1.checkdt like :checkdt
                    and t1.checkno like :checkno
                """);
        List<Map<String, Object>> rows = this.sqlRunner.getRows(sql.toString(), dicParam);

        if (rows.isEmpty()) {
            return Collections.emptyMap();
        }
        // 기본 정보는 첫 번째 행에서 가져옵니다.
        Map<String, Object> result = new HashMap<>(rows.get(0));

        // 파일 리스트 가져오기
//        List<Map<String, Object>> filelist = new ArrayList<>();

//        for (Map<String, Object> row : rows) {
//            if (row.get("t2_spworkcd") != null) {
//                Map<String, Object> fileData = new HashMap<>();
//                fileData.put("spworkcd", row.get("t2_spworkcd"));
//                fileData.put("spcompcd", row.get("t2_spcompcd"));
//                fileData.put("spplancd", row.get("t2_spplancd"));
//                fileData.put("checkdt", row.get("t2_checkdt"));
//                fileData.put("checkseq", row.get("t2_checkseq"));
//                fileData.put("fileseq", row.get("t2_fileseq"));
//                fileData.put("filepath", row.get("filepath"));
//                fileData.put("filesvnm", row.get("filesvnm"));
//                fileData.put("fileextns", row.get("fileextns"));
//                fileData.put("fileurl", row.get("fileurl"));
//                fileData.put("fileornm", row.get("fileornm"));
//                fileData.put("filesize", row.get("filesize"));
//                fileData.put("filerem", row.get("filerem"));
//                fileData.put("repyn", row.get("repyn"));
//                fileData.put("indatem", row.get("indatem"));
//                fileData.put("inuserid", row.get("inuserid"));
//                fileData.put("inusernm", row.get("inusernm"));
//                filelist.add(fileData);
//            }
//        }
//
        //        result.put("filelist", filelist);

        // 점검사항 가져오기
        List<Map<String, Object>> inspectionItems = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("seq", row.get("seq"));
            item.put("inspecdivision", row.get("inspecdivision"));
            item.put("inspeccont", row.get("inspeccont"));
            item.put("inspecresult", row.get("inspecresult"));
            item.put("inspecreform", row.get("inspecreform"));
            item.put("inspecnum", row.get("inspecnum"));
            inspectionItems.add(item);
        }

        result.put("inspectionItems", inspectionItems);
        return result;
    }

    // delete
    @Transactional
    public Boolean delete(TB_RP720_PK pk) {

        try {
            // TB_RP750 삭제
            Optional<TB_RP720> tbRp720 = TBRP720Repository.findById(pk);
            tbRp720.ifPresent(tb_rp720 -> TBRP720Repository.delete(tb_rp720));

            // TB_INSPEC 삭제
            List<TB_INSPEC> tbInspecList = tb_inspecRepository.findAllBySpworkcdAndSpcompcdAndSpplancdAndCheckdtAndCheckno(
                    pk.getSpworkcd(), pk.getSpcompcd(), pk.getSpplancd(), pk.getCheckdt(), pk.getCheckno());
            tb_inspecRepository.deleteAll(tbInspecList);

            return true;

        } catch (Exception e) {
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

}
