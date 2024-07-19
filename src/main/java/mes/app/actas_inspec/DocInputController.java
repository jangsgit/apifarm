package mes.app.actas_inspec;


import mes.app.actas_inspec.service.DocService;
import mes.config.Settings;
import mes.domain.entity.actasEntity.TB_RP770;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP770Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/inspec_doc")
public class DocInputController {

    @Autowired
    TB_RP770Repository tb_rp770Repository;

    @Autowired
    Settings settings;

    private final DocService docService;

    public DocInputController(DocService docService){
        this.docService = docService;
    }


//    @GetMapping("/read")
//    public AjaxResult getList(@RequestParam(value = "searchusr", required = false) String searchusr){
//        List<Map<String, Object>> items = new ArrayList<>();
//
//        if(searchusr == null){
//            searchusr = "";
//        }
//
//        items = this.docService.getInspecList(searchusr);
//
//        AjaxResult result = new AjaxResult();
//        result.data = items;
//
//        return result;
//    }

    @PostMapping("/save")
    @Transactional
    public AjaxResult saveFileter(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "standdt", required = false) String standdt,
            @RequestParam(value = "docdv", required = false) String docdv,
            @RequestParam(value = "standcontent", required = false) String standcontent,
            @RequestParam("filelist")MultipartFile files
            ) throws IOException {

        AjaxResult result = new AjaxResult();

        TB_RP770 tbRp760dto = new TB_RP770();





        if(files != null){

            String path = settings.getProperty("file_upload_path") + "문서관리";

            float fileSize = (float) files.getSize();

            if(fileSize > 52428800){
                result.message = "파일의 크기가 초과하였습니다.";
                return result;
            }

            String fileName = files.getOriginalFilename();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            String file_uuid_name = UUID.randomUUID().toString() + "." + ext;
            String saveFilePath = path;
            File saveDir = new File(saveFilePath);
            MultipartFile mFile = null;

            mFile = files;

            //디렉토리 없으면 생성
            if(!saveDir.isDirectory()){
                saveDir.mkdirs();
            }

            File saveFile = new File(path + File.separator + file_uuid_name);
            mFile.transferTo(saveFile);

            tbRp760dto.setFilepath(saveFilePath);
            tbRp760dto.setFilesvnm(file_uuid_name);
            tbRp760dto.setFileextns(ext);
            tbRp760dto.setFileornm(fileName);
            tbRp760dto.setFilesize(fileSize);
            tbRp760dto.setRepyn("N");
        }



        String checkdtconvertvalue = standdt.replaceAll("-","");

        String formattedValue;
        Optional<String> checknovalue = tb_rp770Repository.findMaxChecknoByCheckdt(checkdtconvertvalue);
        if(checknovalue.isPresent()){

            Integer checknointvalue = Integer.parseInt(checknovalue.get()) + 1;

            formattedValue = String.format("%02d", checknointvalue);

        }else{
            formattedValue = "01";
        }

        tbRp760dto.setSpworkcd("001");
        tbRp760dto.setSpworknm("엑타스");
        tbRp760dto.setSpcompcd("001");
        tbRp760dto.setSpcompnm("발전산단명");
        tbRp760dto.setSpplancd("001");
        tbRp760dto.setSpplannm("발전소명");
        tbRp760dto.setStanddt(checkdtconvertvalue);
        tbRp760dto.setDocdv(docdv);
        tbRp760dto.setCheckseq(formattedValue);
        tbRp760dto.setStandcontent(standcontent);
        tbRp760dto.setTitle(title);
        tbRp760dto.setInusernm("홍길동"); //TODO: 로그인 관련 시스템이 잡히면 나중에 추가

        boolean successcode = docService.save(tbRp760dto);
        if (successcode) {
            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }
        return result;

    }
}
