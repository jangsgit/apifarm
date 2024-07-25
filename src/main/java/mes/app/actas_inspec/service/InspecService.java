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

            String path = settings.getProperty("file_upload_path") + "순회점검일지첨부파일";

            List<String> divisionList = new ArrayList<>();
            List<String> contList = new ArrayList<>();
            List<String> resultList = new ArrayList<>();
            List<String> reformList = new ArrayList<>();
            List<Integer> NumList = new ArrayList<>();

            int num = 0;
            for(String doc : doc_list){
                String[] parts = doc.split("@", -1);

                if(parts.length >= 4){

                    ++num;
                    divisionList.add(parts[0].trim());
                    contList.add(parts[1].trim());
                    resultList.add(parts[2].trim());
                    reformList.add(parts[3].trim());
                    NumList.add(num);
                }

            }

            for(int i=0; i < doc_list.size(); i++){
                TB_INSPEC tb_inspec = new TB_INSPEC();

                int MaxSeq;
                Optional<Integer> SeqValue = tb_inspecRepository.findTopByOrderBySeqDesc();
                MaxSeq = SeqValue.orElse(1);

                tb_inspec.setSeq(MaxSeq + 1);
                tb_inspec.setSpworkcd("001");
                tb_inspec.setSpworknm("대구");
                tb_inspec.setSpcompcd("001");
                tb_inspec.setSpcompnm("대구성서공단");
                tb_inspec.setSpplancd("001");
                tb_inspec.setSpplannm("KT대구물류센터 연료전지발전소");
                tb_inspec.setTabletype("TB_RP710");
                tb_inspec.setSpuncode_id(tbRp710.getSpuncode());
                tb_inspec.setInspecnum(NumList.get(i));
                tb_inspec.setInspecdivision(divisionList.get(i));
                tb_inspec.setInspeccont(contList.get(i));
                tb_inspec.setInspecresult(resultList.get(i));
                tb_inspec.setInspecreform(reformList.get(i));
                tb_inspecRepository.save(tb_inspec);


            }

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

    public void TB_RP715_Save(String spuncode, Map<String, Object> fileinform, String repyn){


            TB_RP715 attachedFile = new TB_RP715();

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
                    select inspecdivision, inspeccont, inspecresult, inspecreform
                    from tb_inspec
                    where "spuncode_id" = :spuncode
                    and "tabletype" = 'TB_RP710'
                    order by inspecnum
                """;
        return this.sqlRunner.getRows(sql, dicParam);
    }

    public List<Map<String, Object>> getInspecList(String searchusr, String searchfrdate, String searchtodate, String spuncode) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql;
        if(!spuncode.isEmpty()){

            dicParam.addValue("spuncode", spuncode);


            sql = """
                select
                supplier, checkdt, checkusr, checkarea, 'Y' as downloads, 'Y' upload 
                from tb_rp710
                where 1 = 1
               and "spuncode" like :spuncode
               """;
        }else {
            dicParam.addValue("paramusr", "%" +searchusr+ "%");
            dicParam.addValue("searchfrdate", searchfrdate.replaceAll("-", ""));
            dicParam.addValue("searchtodate", searchtodate.replaceAll("-", ""));
             sql = """
                     select
                      sb.*,
                      "checkstdt" || '~' || "checkendt" as checktmdt,
                      'Y' as downloads,
                      'Y' as upload,
                      coalesce(
                      	(select sa.filesvnm from tb_rp715 sa where sa.spuncode_id = sb.spuncode and sa.repyn = 'Y'
                      	order by sa.indatem desc limit 1), '') as filesvnm
                      from
                      tb_rp710 sb
                      WHERE 1 = 1
                          AND "checkusr" LIKE :paramusr
                          AND "checkdt" BETWEEN :searchfrdate AND :searchtodate
                      ORDER BY
                          sb.indatem DESC;
                     """;
        }
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }


}
