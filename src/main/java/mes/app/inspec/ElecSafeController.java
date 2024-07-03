package mes.app.inspec;

import mes.app.inspec.service.ElecSafeService;
import mes.domain.entity.ElecSafe;
import mes.domain.entity.ElecSafePk;
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
                                   @RequestParam(value = "title", required=false) String title, // 제목
                                   @RequestParam(value = "inspec_year", required=false) String inspec_year, // 임시 점검년도
                                   @RequestParam(value = "inspec_date1", required=false) String inspec_date, // 임시 점검일자
                                   @RequestParam(value = "doc_select", required=false) String doc_select, // 임시 문서구분
                                   @RequestParam(value = "inspec_loc", required=false) String inspec_loc, // 임시 점검장소
                                   @RequestParam(value = "endresult", required=false) String endresult, // 점검사항
                                   @RequestParam(value = "inusernm", required=false) String inusernm,
                                   @RequestParam(value = "filelist", required=false) MultipartFile[] files,
                                   Authentication auth) {


        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ElecSafePk pk = new ElecSafePk();
        pk.setSpworkcd("001");
        pk.setSpcompcd("002");
        pk.setSpplancd("002");
        pk.setChecksdt("20240701");
        pk.setCheckedt("20240703");

        String c_inspec_date = inspec_date.replaceAll("-","");

        ElecSafe elecSafe = new ElecSafe();
        elecSafe.setId(pk);
        elecSafe.setSpworknm("대구");
        elecSafe.setSpcompnm("00산단");
        elecSafe.setSpplannm("00발전소");
        elecSafe.setTitle(title);
        elecSafe.setEndresult(endresult);
        elecSafe.setInspecdt(c_inspec_date);
        elecSafe.setInspecloc(inspec_loc);
        elecSafe.setIndatem(now);
        elecSafe.setInusernm(inusernm);
        elecSafe.setInusernm(user.getUsername());
        elecSafe.setInuserid(String.valueOf(user.getId()));

        AjaxResult result = new AjaxResult();

        elecSafe = elecSafeRepository.save(elecSafe);

        result.data = elecSafe;

        return result;


    }

}
