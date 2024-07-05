package mes.app.actas_inspec;

import mes.app.actas_inspec.service.ElecSafeService;
import mes.domain.entity.actasEntity.TB_RP750;
import mes.domain.entity.actasEntity.TB_RP750_PK;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.repository.ElecSafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/inspec/elec_safe")
public class ElecSafeController {

    @Autowired
    ElecSafeService elecSafeService;

    @Autowired
    ElecSafeRepository elecSafeRepository;

    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate,
                              @RequestParam(value = "searchusr", required = false) String searchusr){
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

        items = this.elecSafeService.getInspecList(searchusr, startDate, endDate);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult saveElecSafe(@RequestParam(value = "spworkcd", required=false) String spworkcd,
                                   @RequestParam(value = "spcompcd", required=false) String spcompcd,
                                   @RequestParam(value = "spplancd", required=false) String spplancd,
                                   @RequestParam(value = "checksdt", required=false) String checksdt,
                                   @RequestParam(value = "checkedt", required=false) String checkedt,
                                   @RequestParam(value = "title", required=false) String title,
                                   @RequestParam(value = "resistdt", required=false) String resistdt,
                                   @RequestParam(value = "inspec_date1", required=false) String inspec_date,
                                   @RequestParam(value = "inspec_result", required=false) String inspec_result,
                                   @RequestParam(value = "inspec_loc", required=false) String inspec_loc,
                                   @RequestParam(value = "endresult", required=false) String endresult,
                                   @RequestParam(value = "inusernm", required=false) String inusernm,
                                   @RequestParam(value = "filelist", required=false) MultipartFile[] files,
                                   Authentication auth) {

        Random random = new Random();

        // 3자리 랜덤 숫자를 생성 (000 ~ 999)
        int randomValue = random.nextInt(1000);

        // 3자리 문자열로 포맷 (001, 002, ..., 999)
        String formattedValue = String.format("%03d", randomValue);


        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        TB_RP750_PK pk = new TB_RP750_PK();
        pk.setSpworkcd(formattedValue);
        pk.setSpcompcd("002");
        pk.setSpplancd("002");
        pk.setChecksdt("20240701");
        pk.setCheckedt("20240703");

        String c_inspec_date = inspec_date.replaceAll("-","");
        String c_regist_date = resistdt.replaceAll("-","");
        TB_RP750 TBRP750 = new TB_RP750();
        TBRP750.setId(pk);
        TBRP750.setSpworknm("대구");
        TBRP750.setSpcompnm("성서산단");
        TBRP750.setSpplannm("발전소명");
        TBRP750.setTitle(title);
        TBRP750.setRegistdt(c_regist_date);
        TBRP750.setInspecdt(c_inspec_date);
        TBRP750.setInspecresult(inspec_result);
        TBRP750.setInspecloc(inspec_loc);
        TBRP750.setEndresult(endresult);
        TBRP750.setIndatem(now);
        TBRP750.setInusernm(inusernm);
        TBRP750.setInusernm(user.getUsername());
        TBRP750.setInuserid(String.valueOf(user.getId()));

        AjaxResult result = new AjaxResult();

        TBRP750 = elecSafeRepository.save(TBRP750);

        result.data = TBRP750;

        return result;


    }

}
