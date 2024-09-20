package mes.app.actas_inspec.service;


import mes.app.actas_inspec.FileController;
import mes.config.Settings;
import mes.domain.entity.actasEntity.TB_INSPEC;
import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.entity.actasEntity.TB_RP715;
import mes.domain.repository.actasRepository.TB_INSPECRepository;
import mes.domain.repository.actasRepository.TB_RP710Repository;
import mes.domain.repository.actasRepository.TB_RP715Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
public class InspecService {

    @Autowired
    TB_RP710Repository tb_rp710Repository;

    @Autowired
    TB_RP715Repository tb_rp715Repository;

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    Settings settings;

    @Autowired
    TB_INSPECRepository tb_inspecRepository;

    @Autowired
    FileUploaderService fileService;

    @Autowired
    FileController fileController;



    @Transactional
    public Boolean save(TB_RP710 tbRp710, MultipartFile[] files, List<String> doc_list){

        try {

            tb_rp710Repository.save(tbRp710);

            TB_INSPEC_SAVE(doc_list, tbRp710.getSpuncode());


            String path = settings.getProperty("file_upload_path") + "순회점검일지첨부파일";



            if(files != null){
                for (MultipartFile filelist: files){

                    Map<String, Object> fileinform =  fileService.saveFiles(filelist, path); //DISK 저장

                    TB_RP715_Save(tbRp710.getSpuncode(), fileinform, "N");  //DB 저장
                }


            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public void TB_INSPEC_SAVE(List<String> doc_list, String spuncode_id){

        List<String> divisionList = new ArrayList<>();
        List<String> contList = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        List<String> reformList = new ArrayList<>();
        List<Integer> NumList = new ArrayList<>();
        List<Integer> seqList = new ArrayList<>();


        int num = 0;
        for(String doc : doc_list){
            String[] parts = doc.split("@", -1);

            if(parts.length >= 4){

                ++num;
                divisionList.add(parts[0].trim());
                contList.add(parts[1].trim());
                resultList.add(parts[2].trim());
                reformList.add(parts[3]);
                NumList.add(num);
                if(!parts[4].trim().isEmpty()){
                    seqList.add(Integer.valueOf(parts[4].trim()));
                }
            }

        }

        List<Map<String, Object>> getInspecDocList = getInspecDocList(spuncode_id);

        //수정
        if(!getInspecDocList.isEmpty()){
            for(int i = 0; i < doc_list.size(); i++){
                TB_INSPEC tb_inspec = new TB_INSPEC();

                tb_inspec.setSpuncode_id(getInspecDocList.get(i).get("spuncode_id").toString());
                tb_inspec.setSeq((Integer) getInspecDocList.get(i).get("seq"));
                tb_inspec.setSpworkcd("001");
                tb_inspec.setSpworknm("대구");
                tb_inspec.setSpcompcd("001");
                tb_inspec.setSpcompnm("대구성서공단");
                tb_inspec.setSpplancd("001");
                tb_inspec.setSpplannm("KT대구물류센터 연료전지발전소");
                tb_inspec.setTabletype("TB_RP710");

                tb_inspec.setInspecnum(NumList.get(i));
                tb_inspec.setInspecdivision(divisionList.get(i));
                tb_inspec.setInspeccont(contList.get(i));
                tb_inspec.setInspecresult(resultList.get(i));
                tb_inspec.setInspecreform(reformList.get(i));
                tb_inspecRepository.save(tb_inspec);
            }
        }
        else {

            for (int i = 0; i < doc_list.size(); i++) {
                TB_INSPEC tb_inspec = new TB_INSPEC();

                int MaxSeq;
                Optional<Integer> SeqValue = tb_inspecRepository.findTopByOrderBySeqDesc();
                MaxSeq = SeqValue.orElse(1);
                tb_inspec.setSpuncode_id(spuncode_id);

                tb_inspec.setSeq(MaxSeq + 1);
                tb_inspec.setSpworkcd("001");
                tb_inspec.setSpworknm("대구");
                tb_inspec.setSpcompcd("001");
                tb_inspec.setSpcompnm("대구성서공단");
                tb_inspec.setSpplancd("001");
                tb_inspec.setSpplannm("KT대구물류센터 연료전지발전소");
                tb_inspec.setTabletype("TB_RP710");

                tb_inspec.setInspecnum(NumList.get(i));
                tb_inspec.setInspecdivision(divisionList.get(i));
                tb_inspec.setInspeccont(contList.get(i));
                tb_inspec.setInspecresult(resultList.get(i));
                tb_inspec.setInspecreform(reformList.get(i));
                tb_inspecRepository.save(tb_inspec);


            }
        }
    }

    public void TB_RP715_Save(String spuncode, Map<String, Object> fileinform, String repyn){


        TB_RP715 attachedFile = new TB_RP715();

        List<Map<String, Object>> fileItem = getFileList(spuncode);

        String fileName = (String) fileinform.get("fileName");

        for(Map<String, Object> item : fileItem){
            String fileOrNm = (String) item.get("fileornm");
            if(fileName.equals(fileOrNm)){
                return;
            }
        }


        String formattedFileValue;
        Optional<String> checkseqvalue = tb_rp715Repository.findMaxChecknoByCheckdt(spuncode);
        if(checkseqvalue.isPresent()){
            Integer checknointvalue = Integer.parseInt(checkseqvalue.get()) + 1;
            formattedFileValue = String.format("%02d", checknointvalue);
        } else {
            formattedFileValue = "01";
        }

        attachedFile.setSpworkcd("001");
        attachedFile.setSpcompcd("001");
        attachedFile.setSpplancd("001");
        attachedFile.setSpuncode_id(spuncode);
        attachedFile.setSpworknm("관할지역명");
        attachedFile.setSpcompnm("발전산단명");
        attachedFile.setSpplannm("발전소명");
        attachedFile.setCheckseq(formattedFileValue);
        attachedFile.setFilepath(fileinform.get("saveFilePath").toString());
        attachedFile.setFilesvnm(fileinform.get("file_uuid_name").toString());
        attachedFile.setFileextns(fileinform.get("ext").toString());
        attachedFile.setFileornm(fileinform.get("fileName").toString());
        attachedFile.setFilesize((Float) fileinform.get("fileSize"));
        attachedFile.setRepyn(repyn);
        attachedFile.setInuserid("홍길동");
        attachedFile.setInusernm("홍길동");

        tb_rp715Repository.save(attachedFile);


    }

    public List<Map<String, Object>> getFileList(String SpunCode){

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        dicParam.addValue("spuncode", SpunCode);

        String sql = """
                    select filepath, filesvnm
                    from tb_rp715
                    where "spuncode_id" = :spuncode
                    order by checkseq
                """;
        return this.sqlRunner.getRows(sql, dicParam);
    }

    public List<Map<String, Object>> getInspecDocList(String spuncode){

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        dicParam.addValue("spuncode", spuncode);

        String sql = """
                    select inspecdivision, inspeccont, inspecresult, inspecreform, seq, spuncode_id
                    from tb_inspec
                    where "spuncode_id" = :spuncode
                    and "tabletype" = 'TB_RP710'
                    order by inspecnum
                """;
        return this.sqlRunner.getRows(sql, dicParam);
    }

    public List<Map<String, Object>> getInspecList(String searchusr, String searchfrdate, String searchtodate, String spuncode, String searchflag,
                                                   String spworkcd, String spcompcd, String spplancd
    ) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql;
        if(!spuncode.isEmpty()){

            dicParam.addValue("spuncode", spuncode);


            sql = """
                select
                spworkcd, spworknm, spcompcd, spcompnm, spplannm, spplancd, supplier, checkdt, checkusr, checkarea, 'Y' as downloads, 'Y' upload, spuncode, checkno 
                from tb_rp710
                where 1 = 1
               and "spuncode" like :spuncode
               """;
        }else {
            if(searchflag == null){
                searchflag = "";
            }
            dicParam.addValue("paramusr", "%" +searchusr+ "%");
            dicParam.addValue("searchfrdate", searchfrdate.replaceAll("-", ""));
            dicParam.addValue("searchtodate", searchtodate.replaceAll("-", ""));
            dicParam.addValue("searchflag", "%" +searchflag+ "%");
            dicParam.addValue("spplancd",  spplancd);
            dicParam.addValue("spworkcd", spworkcd);
            dicParam.addValue("spcompcd", spcompcd);


            sql = """
                     select
                      sb.*,
                      checkstdt || ' ~ ' || checkendt as checktmdt,
                      CASE WHEN COALESCE((SELECT sa.fileornm FROM tb_rp715 sa WHERE sa.spuncode_id = sb.spuncode AND sa.repyn = 'Y' ORDER BY sa.indatem DESC LIMIT 1),'') != '' THEN 'Y'
                      ELSE 'N'
                      END AS downloads,
                      'Y' as upload,
                      coalesce(
                      	(select sa.fileornm from tb_rp715 sa where sa.spuncode_id = sb.spuncode and sa.repyn = 'Y'
                      	order by sa.indatem desc limit 1), '') as fileornm
                      from
                      tb_rp710 sb
                      WHERE 1 = 1
                          AND "checkusr" LIKE :paramusr
                          AND "checkdt" BETWEEN :searchfrdate AND :searchtodate
                          AND "flag" LIKE :searchflag
                     """;
            if(spworkcd != null){
                sql += " AND \"spworkcd\" = :spworkcd";
            }
            if(spcompcd != null){
                sql += " AND \"spcompcd\" = :spcompcd";
            }
            if(spplancd != null){
                sql += " AND \"spplancd\" = :spplancd";
            }
            sql += " ORDER BY sb.indatem DESC;";

        }
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }


    public Map<String, Object> findById(String spuncode) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("spuncode", spuncode);

        sql.append("""
                select 
                    a.*,
                    b.filepath as filepath,
                    b.filesvnm as filesvnm,
                    b.fileextns as fileextns,
                    b.fileornm as fileornm,
                    b.filesize as filesize,
                    b.spuncode_id as spuncode_id,
                    b.indatem as indatem,
                    b.checkseq as checkseq
                from tb_rp710 a  
                left join tb_rp715 b
                on 
                    a.spuncode = b.spuncode_id
                    and b.repyn <> 'Y'
                where
                    spuncode like :spuncode
                    
                    
                """);
        List<Map<String, Object>> rows = this.sqlRunner.getRows(sql.toString(), dicParam);

        if(rows.isEmpty()){
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>(rows.get(0));
        List<Map<String, Object>> filelist = new ArrayList<>();

        for(Map<String, Object> row : rows){
            if(row.get("spuncode_id") != null){
                Map<String, Object> fileData = new HashMap<>();
                fileData.put("filepath", row.get("filepath"));
                fileData.put("filesvnm", row.get("filesvnm"));
                fileData.put("fileextns", row.get("fileextns"));
                fileData.put("fileornm", row.get("fileornm"));
                fileData.put("filesize", row.get("filesize"));
                fileData.put("spuncode_id", row.get("spuncode_id"));
                fileData.put("checkseq", row.get("checkseq"));
                fileData.put("indatem", row.get("indatem"));
                filelist.add(fileData);

            }
        }
        result.put("filelist", filelist);
        return result;

    }

    @Transactional
    public List<String> getSuggestions(String query, String field) {

        List<String> rawResults = switch (field) {
            case "supplier" -> tb_rp710Repository.findSuppliersByQuery(query);
            case "checkarea" -> tb_rp710Repository.findCheckareasByQuery(query);
            case "checkusr" -> tb_rp710Repository.findCheckusrsByQuery(query);
            default -> new ArrayList<>();
        };

        // checkusr 필드일 경우에만 쉼표로 분리하고 중복을 제거하는 로직을 적용
        if ("checkusr".equals(field)) {
            return rawResults.stream()
                    .flatMap(result -> Arrays.stream(result.split(",")))  // 쉼표로 분리
                    .map(String::trim)  // 각 이름의 앞뒤 공백을 제거
                    .filter(name -> name.toLowerCase().contains(query.toLowerCase()))  // 검색어를 포함한 결과만 필터링
                    .distinct()  // 중복된 이름 제거
                    .collect(Collectors.toList());  // 최종 리스트로 변환
        } else {
            // 다른 필드일 경우 기존 결과 반환 (필터링 없이 그대로)
            return rawResults.stream()
                    .filter(result -> result.toLowerCase().contains(query.toLowerCase()))  // 검색어를 포함한 결과만 필터링
                    .collect(Collectors.toList());
        }
    }
}
