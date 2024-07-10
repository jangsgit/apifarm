package mes.app.actas_inspec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mes.app.actas_inspec.service.HapReportService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.*;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP720Repository;
import mes.domain.repository.TB_RP725Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/inspec/hap")
public class HapReportController {

    @Autowired
    HapReportService hapReportService;

    @Autowired
    TB_RP720Repository tb_rp720Repository;

    @Autowired
    TB_RP725Repository tp_rp725Repository;
    @Autowired
    private Settings settings;

    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate,
                              @RequestParam(value = "searchusr", required = false) String searchusr) {
        List<Map<String, Object>> items = new ArrayList<>();

        if (searchusr == null) {
            searchusr = "";
        }

        if (startDate == null) {
            startDate = "";
        }

        if (endDate == null) {
            endDate = "";
        }

        items = this.hapReportService.getInspecList(searchusr, startDate, endDate);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult saveHapReport(@RequestParam Map<String, String> params,
                                    @RequestParam(value = "filelist", required = false) MultipartFile files,
                                    @RequestParam("inspectlist") String inspectlistJson,
                                    Authentication auth) throws IOException {

        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AjaxResult result = new AjaxResult();

        String newKey = "";
        String nspworkcd = params.get("spworkcd");
        String nspcompcd = params.get("spcompcd");
        String nspplancd = params.get("spplancd");

        if (nspworkcd != null && nspcompcd != null && nspplancd != null) {

            Optional<String> checknovalue = tb_rp720Repository.findMaxNum(nspworkcd, nspcompcd, nspplancd);
            if (checknovalue.isPresent()) {
                Integer checknointvalue = Integer.parseInt(checknovalue.get()) + 1;
                newKey = checknointvalue.toString();
            } else {
                newKey = "1";
            }
        }

        String c_checkdt = params.get("checkdt").replaceAll("-","");

        TB_RP720_PK pk = new TB_RP720_PK();
        pk.setSpworkcd(nspworkcd);
        pk.setSpcompcd(nspcompcd);
        pk.setSpplancd(nspplancd);
        pk.setCheckdt(c_checkdt);
        pk.setCheckno(newKey);

        TB_RP720 tbRp720 = new TB_RP720();

        if(files != null){

            String path = settings.getProperty("file_upload_path") + "합동점검";

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

            tbRp720.setFilepath(saveFilePath);
            tbRp720.setFilesvnm(file_uuid_name);
            tbRp720.setFileornm(fileName);
            tbRp720.setFilesize(fileSize);
//            tbRp720.setFileextns(params.get("fileextns"));
//            tbRp720.setFilerem();
        }

        // JSON 문자열을 List<Map<String, String>>로 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> inspectlist = objectMapper.readValue(inspectlistJson, new TypeReference<List<Map<String, String>>>() {});


        List<Boolean> saveResults = new ArrayList<>();

        tbRp720.setPk(pk);
        tbRp720.setSpworknm(params.get("spworknm"));
        tbRp720.setSpcompnm(params.get("spcompnm"));
        tbRp720.setSpplannm(params.get("spplannm"));
        tbRp720.setChecknm(params.get("checknm"));
        tbRp720.setCheckusr(params.get("checkusr"));
        tbRp720.setCheckresult(params.get("checkresult"));
        tbRp720.setCheckrem(params.get("checkrem"));
        tbRp720.setIndatem(now);
        tbRp720.setInuserid(String.valueOf(user.getId()));
        tbRp720.setInusernm(user.getUsername());

        // 점검리스트 저장
        for(int i=0; i < inspectlist.size(); i++){
            Map<String, String> item = inspectlist.get(i);

            TB_RP725_PK pk2 = new TB_RP725_PK();
            pk2.setSpworkcd(nspworkcd);
            pk2.setSpcompcd(nspcompcd);
            pk2.setSpplancd(nspplancd);
            pk2.setCheckdt(c_checkdt);
            pk2.setCheckno(newKey);
            pk2.setCheckseq(item.get("checkseq"));

            TB_RP725 tbRp725 = new TB_RP725();
            tbRp725.setPk(pk2);
            tbRp725.setSpworknm(params.get("spworknm"));
            tbRp725.setSpcompnm(params.get("spcompnm"));
            tbRp725.setSpplannm(params.get("spplannm"));
            tbRp725.setCheckobj(item.get("checkobj"));
            tbRp725.setCheckitem(item.get("checkitem"));
            tbRp725.setIndatem(now);
            tbRp720.setInuserid(String.valueOf(user.getId()));
            tbRp720.setInusernm(user.getUsername());
            boolean successcode2 = hapReportService.save2(tbRp725);
            saveResults.add(successcode2);
        }

        boolean successcode = hapReportService.save(tbRp720);

        result.data = tbRp720;

        // 점검리스트 저장 결과 확인
        boolean allSuccess = saveResults.stream().allMatch(Boolean::booleanValue);

        if (successcode && allSuccess) {
            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }

        return result;


    }

}
