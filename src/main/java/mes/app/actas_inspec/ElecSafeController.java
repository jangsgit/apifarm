package mes.app.actas_inspec;

import mes.app.actas_inspec.service.ElecSafeService;
import mes.domain.entity.actasEntity.TB_RP750;
import mes.domain.entity.actasEntity.TB_RP750_PK;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP750Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/inspec/elec_safe")
public class ElecSafeController {

    @Autowired
    ElecSafeService elecSafeService;

    @Autowired
    TB_RP750Repository TBRP750Repository;

    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate,
                              @RequestParam(value = "searchtitle", required = false) String searchTitle) {

        if (searchTitle == null) {
            searchTitle = "";
        }

        if (startDate == null) {
            startDate = "";
        }

        if (endDate == null) {
            endDate = "";
        }

        String c_startDate = startDate.replaceAll("-", "");
        String c_endDate = endDate.replaceAll("-", "");

        List<Map<String, Object>> items = this.elecSafeService.getList(searchTitle, c_startDate, c_endDate);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult saveElecSafe(@RequestParam Map<String, String> params,
                                   @RequestParam(value = "filelist", required = false) MultipartFile[] files,
                                   Authentication auth) {

        Random random = new Random();

        // 3자리 랜덤 숫자를 생성 (000 ~ 999)
        int randomValue = random.nextInt(1000);

        // 3자리 문자열로 포맷 (001, 002, ..., 999)
        String formattedValue = String.format("%03d", randomValue);

        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        TB_RP750_PK pk = new TB_RP750_PK();
        pk.setSpworkcd(params.get("spworkcd"));
        pk.setSpcompcd(params.get("spcompcd"));
        pk.setSpplancd(params.get("spplancd"));

        // 임시로 pk
        if (params.get("checksdt") == null && params.get("checksdt") == null) {
            pk.setChecksdt(formattedValue);
            pk.setCheckedt(formattedValue);
        } else{
            pk.setChecksdt(params.get("checksdt"));
            pk.setCheckedt(params.get("checkedt"));
        }

        String c_inspec_date = params.get("inspecdt").replaceAll("-", "");
        String c_regist_date = params.get("registdt").replaceAll("-", "");

        TB_RP750 TBRP750 = new TB_RP750();
        TBRP750.setId(pk);
        TBRP750.setSpworknm(params.get("spworknm"));
        TBRP750.setSpcompnm(params.get("spcompnm"));
        TBRP750.setSpplannm(params.get("spplannm"));
        TBRP750.setTitle(params.get("title"));
        TBRP750.setRegistdt(c_regist_date);
        TBRP750.setInspecdt(c_inspec_date);
        TBRP750.setInspecresult(params.get("inspecresult"));
        TBRP750.setInspecloc(params.get("inspecloc"));
        TBRP750.setDocselect(params.get("docselect"));
        TBRP750.setEndresult(params.get("endresult"));
        TBRP750.setIndatem(now);
        TBRP750.setInusernm(user.getUsername());
        TBRP750.setInuserid(String.valueOf(user.getId()));

        AjaxResult result = new AjaxResult();

        boolean successcode = elecSafeService.save(TBRP750);

        result.data = TBRP750;
        if (successcode) {
            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }

        return result;

    }

    @DeleteMapping("/delete")
    public AjaxResult deleteElecSafe(@RequestBody List<TB_RP750_PK> pkList) {
        AjaxResult result = new AjaxResult();

        for (TB_RP750_PK pk : pkList) {
            TB_RP750 TBRP750 = new TB_RP750();
            TBRP750.setId(pk);

            boolean successcode = elecSafeService.delete(TBRP750);
            if (!successcode) {
                result.success = false;
                result.message = "삭제에 실패하였습니다.";
                return result;
            }
        }

        result.success = true;
        result.message = "삭제하였습니다.";
        return result;
    }

    @PostMapping("/modfind")
    public AjaxResult getById(@RequestBody List<TB_RP750_PK> pkList) {
        AjaxResult result = new AjaxResult();

        Optional<TB_RP750> item = elecSafeService.findById(pkList.get(0));
        result.data = item;
        return result;
    }

}
