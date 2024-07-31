package mes.app.actas_inspec;


import mes.app.actas_inspec.service.DocService;
import mes.config.Settings;
import mes.domain.model.AjaxResult;
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


        }



        return result;

    }
}
