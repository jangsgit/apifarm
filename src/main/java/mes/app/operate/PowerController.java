package mes.app.operate;

import mes.app.actas_inspec.service.ElecSafeService;
import mes.app.operate.service.PowerService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP750;
import mes.domain.entity.actasEntity.TB_RP750_PK;
import mes.domain.entity.actasEntity.TB_RP920;
import mes.domain.entity.actasEntity.TB_RP920_PK;
import mes.domain.model.AjaxResult;
import mes.domain.repository.ElecSafeRepository;
import mes.domain.repository.PowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/operate/power")
public class PowerController {

    @Autowired
    private PowerService powerService;

    @Autowired
    private PowerRepository powerRepository;

    @Autowired
    Settings settings;

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

        items = this.powerService.getInspecList(searchusr, startDate, endDate);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult savePower(@RequestParam(value = "spworkcd", required = false) String spworkcd,
                                @RequestParam(value = "spworknm", required = false) String spworknm,
                                @RequestParam(value = "spcompcd", required = false) String spcompcd,
                                @RequestParam(value = "spcompnm", required = false) String spcompnm,
                                @RequestParam(value = "spplannm", required = false) String spplannm,
                                @RequestParam(value = "spwtycd", required = false) String spwtycd,
                                @RequestParam(value = "spwtynm", required = false) String spwtynm,
                                @RequestParam(value = "makercd", required = false) String makercd,
                                @RequestParam(value = "makernm", required = false) String makernm,
                                @RequestParam(value = "setupdt", required = false) String setupdt,
                                @RequestParam(value = "pwcapa", required = false) Double pwcapa,
                                @RequestParam(value = "postno", required = false) String postno,
                                @RequestParam(value = "address1", required = false) String address1,
                                @RequestParam(value = "address2", required = false) String address2,
                                @RequestParam(value = "workyn", required = false) String workyn,
                                @RequestParam(value = "mcltcd", required = false) String mcltcd,
                                @RequestParam(value = "mcltnm", required = false) String mcltnm,
                                @RequestParam(value = "mcltusrnm", required = false) String mcltusrnm,
                                @RequestParam(value = "mcltusrhp", required = false) String mcltusrhp,
                                @RequestParam(value = "remark", required = false) String remark,
                                @RequestParam(value = "filelist", required = false) MultipartFile files,
                                Authentication auth) throws IOException {

        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AjaxResult result = new AjaxResult();
        String newKey = "";

        if (spworkcd != null && spcompcd != null) {

            Optional<String> checknovalue = powerRepository.findMaxChecknoBySpplancd(spworkcd, spcompcd);
            if (checknovalue.isPresent()) {
                Integer checknointvalue = Integer.parseInt(checknovalue.get()) + 1;
                newKey = String.format("%03d", checknointvalue);
            } else {
                newKey = "001";
            }

        }

        TB_RP920_PK pk = new TB_RP920_PK();
        pk.setSpworkcd(spworkcd);
        pk.setSpcompcd(spcompcd);
        pk.setSpplancd(newKey);

        TB_RP920 tbRp920 = new TB_RP920();

        if(files != null){

            String path = settings.getProperty("file_upload_path") + "발전소";

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

            tbRp920.setFilepath(saveFilePath);
            tbRp920.setFilesvnm(file_uuid_name);
            tbRp920.setFileornm(fileName);
            tbRp920.setFilesize(fileSize);
//            tbRp920.setFilerem();
        }

        tbRp920.setPk(pk);
        tbRp920.setSpworknm(spworknm);
        tbRp920.setSpcompnm(spcompnm);
        tbRp920.setSpplannm(spplannm);
        tbRp920.setSpwtycd(spwtycd);
        tbRp920.setSpwtynm(spwtynm);
        tbRp920.setMakercd(makercd);
        tbRp920.setMakernm(makernm);
        tbRp920.setSetupdt(setupdt);
        tbRp920.setPwcapa(pwcapa);
        tbRp920.setPostno(postno);
        tbRp920.setAddress1(address1);
        tbRp920.setAddress2(address2);
        tbRp920.setWorkyn(workyn);
        tbRp920.setMcltcd(mcltcd);
        tbRp920.setMcltnm(mcltnm);
        tbRp920.setMcltusrnm(mcltusrnm);
        tbRp920.setMcltusrhp(mcltusrhp);
        tbRp920.setRemark(remark);
        tbRp920.setIndatem(now);
        tbRp920.setInuserid(String.valueOf(user.getId()));
        tbRp920.setInusernm(user.getUsername());

        boolean suceescode = powerService.save(tbRp920);
        if (suceescode) {
            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }

        return result;
    }
}
