package mes.app.inspec;

import mes.domain.entity.ElecSafe;
import mes.domain.entity.ElecSafePk;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.repository.ElecSafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api/inspec/elec_safe")
public class ElecSafeController {

    @Autowired
    ElecSafeRepository elecSafeRepository;

    @PostMapping("/save")
    public AjaxResult saveElecSafe(@RequestParam(value = "spworkcd", required=false) String spworkcd,
                                   @RequestParam(value = "spcompcd", required=false) String spcompcd,
                                   @RequestParam(value = "spplancd", required=false) String spplancd,
                                   @RequestParam(value = "checksdt", required=false) String checksdt,
                                   @RequestParam(value = "checkedt", required=false) String checkedt,
                                   @RequestParam(value = "title", required=false) String title, // 임시 제목
                                   @RequestParam(value = "inspec_year", required=false) String inspec_year, // 임시 점검년도
                                   @RequestParam(value = "inspec_date", required=false) String inspec_date, // 임시 점검일자
                                   @RequestParam(value = "doc_select", required=false) String doc_select, // 임시 문서구분
                                   @RequestParam(value = "inspec_loc", required=false) String inspec_loc, // 임시 점검장소
                                   @RequestParam(value = "endresult", required=false) String endresult, // 임시 점검사항
                                   @RequestParam(value = "inusernm", required=false) String inusernm,
                                   @RequestParam(value = "filelist", required=false) MultipartFile[] files,
                                   Authentication auth) {


        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ElecSafePk pk = new ElecSafePk();
        pk.setSpworkcd(spworkcd);
        pk.setSpcompcd(spcompcd);
        pk.setSpplancd(spplancd);
        pk.setChecksdt(checksdt);
        pk.setCheckedt(checkedt);

        ElecSafe elecSafe = new ElecSafe();
        elecSafe.setId(pk);
        elecSafe.setEndresult(endresult);
        elecSafe.setIndatem(now);
        elecSafe.setInusernm(inusernm);
        elecSafe.setInuserid(String.valueOf(user.getId()));

        AjaxResult result = new AjaxResult();

//        elecSafe = elecSafeRepository.save(elecSafe);

        result.data = elecSafe;

        return result;


    }

}
