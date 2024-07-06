package mes.app.actas_inspec.service;


import mes.config.Settings;
import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.entity.actasEntity.TB_RP715;
import mes.domain.repository.actasRepository.TB_RP710Repository;
import mes.domain.repository.actasRepository.TB_RP715Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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


    @Transactional
    public Boolean save(TB_RP710 tbRp710, MultipartFile[] files){

        try {

            tb_rp710Repository.save(tbRp710);

            String path = settings.getProperty("file_upload_path") + "순회점검일지첨부파일";


            if(files != null){
                for (MultipartFile filelist: files){

                    String fileName = filelist.getOriginalFilename();

                    String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

                    String file_uuid_name = UUID.randomUUID().toString() + "." + ext;
                    String saveFilePath = path;
                    File saveDir = new File(saveFilePath);
                    MultipartFile mFile = null;

                    mFile = filelist;

                    //디렉토리 없으면 생성
                    if(!saveDir.isDirectory()){
                        saveDir.mkdirs();
                    }

                    File saveFile = new File(path + File.separator + file_uuid_name);
                    mFile.transferTo(saveFile);

                    Float fileSize = (float) filelist.getSize();

                    TB_RP715 attachedFile = new TB_RP715();

                    String formattedFileValue;
                    Optional<String> checkseqvalue = tb_rp715Repository.findMaxChecknoByCheckdt(tbRp710.getSpuncode());
                    if(checkseqvalue.isPresent()){
                        Integer checknointvalue = Integer.parseInt(checkseqvalue.get()) + 1;
                        formattedFileValue = String.format("%02d", checknointvalue);
                    } else {
                        formattedFileValue = "01";
                    }

                    attachedFile.setSpworkcd("001");
                    attachedFile.setSpcompcd("001");
                    attachedFile.setSpplancd("001");
                    attachedFile.setSpuncode_id(tbRp710.getSpuncode());
                    attachedFile.setSpworknm("관할지역명");
                    attachedFile.setSpcompnm("발전산단명");
                    attachedFile.setSpplannm("발전소명");
                    attachedFile.setCheckseq(formattedFileValue);
                    attachedFile.setFilepath(saveFilePath);
                    attachedFile.setFilesvnm(file_uuid_name);
                    attachedFile.setFileextns(ext);
                    attachedFile.setFileornm(fileName);
                    attachedFile.setFilesize(fileSize);
                    attachedFile.setRepyn("N");
                    attachedFile.setInuserid("홍길동");
                    attachedFile.setInusernm("홍길동");

                    tb_rp715Repository.save(attachedFile);
                }


            }

            /*for (TB_RP715 fileEntity : fileEntities) {
                String formattedFileValue;
                Optional<String> checkseqvalue = tb_rp715Repository.findMaxChecknoByCheckdt(fileEntity.getSpuncode_id());

                if (checkseqvalue.isPresent()) {
                    Integer checknointvalue = Integer.parseInt(checkseqvalue.get()) + 1;
                    formattedFileValue = String.format("%02d", checknointvalue);
                } else {
                    formattedFileValue = "01";
                }

                // Set the checkseq value for the current file entity
                fileEntity.setCheckseq(formattedFileValue);

                // Save the file entity
                tb_rp715Repository.save(fileEntity);
            }*/
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }


    public List<Map<String, Object>> getInspecList(String searchusr) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();


        dicParam.addValue("paramusr", "%" +searchusr+ "%");

        String sql = """
                select 
                ROW_NUMBER() OVER (ORDER BY indatem DESC) AS rownum,
                *, "checkstdt" || '~' || "checkendt" AS checktmdt 
                from tb_rp710 sb
                where 1 = 1
               and "checkusr" like :paramusr
                order by indatem desc
                """;
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }


}
