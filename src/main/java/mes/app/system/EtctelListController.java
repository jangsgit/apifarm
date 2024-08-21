package mes.app.system;


import mes.app.system.service.EtctelListService;

import mes.domain.entity.TB_RP980;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP980Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.*;

@RestController
@RequestMapping("/api/system/tbRp980")
public class EtctelListController {

    @Autowired
    EtctelListService etctelListService;

    @Autowired
    TB_RP980Repository tp980Repository;

    @Autowired
    SqlRunner sqlRunner;


    @GetMapping("/read")
    public AjaxResult getList(
            @RequestParam(value = "emconper", required = false) String emconper,
            @RequestParam(value= "emconmno", required = false) String emconmno){


        if (emconper == null) {
            emconper = "";
        }
        if (emconmno == null) {
            emconmno = "";
        }


        List<Map<String, Object>> items = this.etctelListService.getEtctelList(emconper, emconmno);
        AjaxResult result = new AjaxResult();
        result.data=items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult addEtctelList(
            @RequestParam(value = "emcontno", required = false) String emcontno,
            @RequestParam(value = "emconcomp", required = false) String emconcomp,
            @RequestParam(value = "emconper", required = false) String emconper,
            @RequestParam(value = "emcontel", required = false) String emcontel,
            @RequestParam(value = "indatem", required = false) Date indatem,
            @RequestParam(value = "inuserid", required = false) String inuserid,
            @RequestParam(value = "inusernm", required = false) String inusernm,
            @RequestParam(value = "emconemail", required = false) String emconemail,
            @RequestParam(value = "spworkcd", required = false) String spworkcd,
            @RequestParam(value = "spcompcd", required = false) String spcompcd,
            @RequestParam(value = "taskwork", required = false) String taskwork,
            @RequestParam(value = "divinm", required = false) String divinm,
            @RequestParam(value = "emconmno", required = false) String emconmno,
            HttpServletRequest request,
            Authentication auth) {

        // 현재 사용자 정보 가져오기
        User user = (User) auth.getPrincipal();

        Date currentDate = new Date(System.currentTimeMillis());


        TB_RP980 tbRp980 = null;

        if (emcontno == null || emcontno.isEmpty()) {
            tbRp980 = new TB_RP980();
            emcontno = etctelListService.generateNewEmcontno(); // 수동으로 ID 생성
            tbRp980.setEmcontno(emcontno);
        } else {
            tbRp980 = this.tp980Repository.findById(emcontno).orElse(new TB_RP980());
            tbRp980.setEmcontno(emcontno); // 기존 ID 사용
        }


        tbRp980.setEmconcomp(emconcomp);    // 협력사 명
            tbRp980.setEmconper(emconper);      // 담당자
            tbRp980.setEmcontel(emcontel);      // 사무실번호
            tbRp980.setEmconmno(emconmno);      // 모바일
            tbRp980.setEmconemail(emconemail);  // 이메일
            tbRp980.setTaskwork(taskwork);
//        tbRp980.setDivinm(divinm);
            tbRp980.setIndatem(currentDate);    // 입력일시
            tbRp980.setInuserid(String.valueOf(user.getId()));  // 입력자 ID
            tbRp980.setInusernm(user.getUsername());    // 입력자 이름

            tbRp980.setSpworkcd(spworkcd);  //관할지역코드
            tbRp980.setSpcompcd(spcompcd); //발전산단코드


        AjaxResult result = new AjaxResult();

        try {

            tbRp980 = this.tp980Repository.save(tbRp980);

        } catch (Exception e) {
            e.printStackTrace();
            result.success = false;
            result.message = "데이터베이스 저장 중 오류가 발생했습니다.";
        }

            result.data = tbRp980;

            return result;
        }
    }

//    @DeleteMapping("/delete")
//    public AjaxResult deleteEtctelList(@RequestParam("emcontno") String emcontno) {
//        AjaxResult result = new AjaxResult();
//
//        if (emcontno != null) {
//            try {
//                tp980Repository.deleteById(emcontno);
//               System.out.println(true);
//                System.out.println("Data deleted successfully");
//            } catch (EmptyResultDataAccessException e) {
//                System.out.println(false);
//                System.out.println("No entity with the specified ID exists");
//            } catch (Exception e) {
//                System.out.println(false);
//                System.out.println("An error occurred while deleting the data");
//            }
//        } else {
//            System.out.println(false);
//            System.out.println("ID is required");
//        }
//
//        return result;
//    }


