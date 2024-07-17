package mes.app.actas_inspec;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import mes.app.actas_inspec.service.InspecService;
import mes.app.common.service.FileService;
import mes.config.Settings;
import mes.domain.DTO.Actas_Fileset;
import mes.domain.entity.AttachFile;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_INSPEC;
import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.entity.actasEntity.TB_RP715;
import mes.domain.model.AjaxResult;
import mes.domain.repository.AttachFileRepository;
import mes.domain.repository.actasRepository.TB_RP710Repository;
import mes.domain.repository.actasRepository.TB_RP715Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inspec_report")
public class InspecController {

    @Autowired
    TB_RP715Repository tb_rp715Repository;

    @Autowired
    FileService attachFileService;

    @Autowired
    AttachFileRepository attachFileRepository;

    private final InspecService inspecService;
    private final Settings settings;
    private final TB_RP710Repository tb_rp710Repository;


    public InspecController(InspecService inspecService, TB_RP710Repository tb_rp710Repository, Settings settings){
        this.inspecService = inspecService;
        this.tb_rp710Repository = tb_rp710Repository;
        this.settings = settings;
    }


    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "searchusr", required = false) String searchusr,
                              @RequestParam(value = "searchfrdate", required = false) String searchfrdate,
                              @RequestParam(value = "searchtodate", required = false) String searchtodate

                              ){
        List<Map<String, Object>> items = new ArrayList<>();

        searchusr = Optional.ofNullable(searchusr).orElse("");
        searchfrdate = Optional.ofNullable(searchfrdate).orElse("20000101");
        searchtodate = Optional.ofNullable(searchtodate).orElse("29991231");

        if(searchfrdate.isEmpty()){
            searchfrdate = "20000101";
        }
        if(searchtodate.isEmpty()){
            searchtodate = "29991231";
        }


        items = this.inspecService.getInspecList(searchusr, searchfrdate, searchtodate);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    //순회점검 일지 저장
    @PostMapping("/save")
    @Transactional
    public AjaxResult saveFilter(
            //@ModelAttribute TB_RP710 tbRp710
            @RequestParam(value = "supplier", required = false) String supplier,
            @RequestParam(value = "checkstdt", required = false) String checkstdt,
            @RequestParam(value = "checkdt", required = false) String checkdt,
            @RequestParam(value = "checkendt", required = false) String checkendt,
            @RequestParam(value = "checkusr", required = false) String checkusr,
            @RequestParam(value = "checkarea", required = false) String checkarea,
            @RequestParam(value = "randomuuid", required = false) String randomuuid,
            @RequestParam(value = "doc-list", required = false) List<String> doc_list,
            @RequestParam(value = "filelist", required = false) MultipartFile[] files
            //@RequestParam Map<String, String> params
            ){

        AjaxResult result = new AjaxResult();



        if(files != null){
            for(MultipartFile filelist : files){
                if(filelist.getSize() > 52428800){
                    result.success = false;
                    result.message = "파일사이즈가 초과하였습니다.";
                    return result;
                }
            }
        }

        TB_RP710 tbRp710dto = new TB_RP710();

        String checkdtconvertvalue = checkdt.replaceAll("-","");

        String formattedValue;
        Optional<String> checknovalue = tb_rp710Repository.findMaxChecknoByCheckdt(checkdtconvertvalue);
        if(checknovalue.isPresent()){

            Integer checknointvalue = Integer.parseInt(checknovalue.get()) + 1;

            formattedValue = String.format("%02d", checknointvalue);

        }else{
            formattedValue = "01";
        }

        tbRp710dto.setSpworkcd("001");
        tbRp710dto.setSpworknm("대구");
        tbRp710dto.setSpcompcd("001");
        tbRp710dto.setSpcompnm("대구성서공단");
        tbRp710dto.setSpplancd("001");
        tbRp710dto.setSpplannm("KT대구물류센터 연료전지발전소");
        tbRp710dto.setCheckdt(checkdtconvertvalue);
        tbRp710dto.setCheckno(formattedValue);
        tbRp710dto.setCheckstdt(checkstdt);
        tbRp710dto.setCheckendt(checkendt);
        tbRp710dto.setCheckusr(checkusr);
        tbRp710dto.setCheckarea(checkarea);

        tbRp710dto.setSupplier(supplier);
        tbRp710dto.setSpuncode(randomuuid);

            String path = settings.getProperty("file_upload_path") + "순회점검일지첨부파일";

            List<TB_RP715> fileEntities = new ArrayList<>();


        boolean successcode = inspecService.save(tbRp710dto, files, doc_list);
            if (successcode) {
                result.success = true;
                result.message = "저장하였습니다.";
            } else {
                result.success = false;
                result.message = "저장에 실패하였습니다.";
            }


        return result;
    }


    @PostMapping("/filesave")
    public AjaxResult fileupload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("spuncode") String spuncode){
        AjaxResult result = new AjaxResult();



        result.success = true;


        return result;
    }


    @PostMapping("/delete")
    @Transactional
    public AjaxResult delete(
            @RequestParam(value = "spuncode") String spuncode
    ){

        AjaxResult result = new AjaxResult();

        ObjectMapper mapper = new ObjectMapper();

        String cleanJson = spuncode.replaceAll("[\\[\\]\"]", "");
        String[] tokens = cleanJson.split(",");

        List<String> paramList = List.of(tokens);

        for(String param : paramList){
            System.out.println(param);
            //TODO: 이거 자식테이블먼저 삭제해야한다.
            tb_rp715Repository.deleteBySpuncodeId(param);
            tb_rp710Repository.deleteBySpuncode(param);
        }


        result.success = true;
        result.message = "성공";
        return result;
    }

    @GetMapping("/download-doc")
    public ResponseEntity<Resource> downloadDoc(){
        try{
            String path = settings.getProperty("file_upload_path") + "순회점검일지양식.docx";
            Path filePath = Paths.get(path);
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists() || resource.isReadable()){
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
