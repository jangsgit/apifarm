package mes.app.system;


import mes.app.system.service.EtctelListService;
import mes.app.system.service.UserService;
import mes.domain.DTO.TB_RP980Dto;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.repository.RelationDataRepository;
import mes.domain.repository.TB_RP980Repository;
import mes.domain.repository.UserRepository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/tel_List")
public class EtctelListController {

    @Autowired
    private UserService userService;

    @Autowired
    EtctelListService etctelListService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RelationDataRepository relationDataRepository;

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


    // 비상연락망 상세정보 조회

//    @GetMapping("/read")
//    public AjaxResult getEtctelList(
//            @RequestParam(value = "group", required = false) Integer group,
//            @RequestParam(value = "keyword", required = false) String keyword,
//            @RequestParam(value = "username", required = false) String username,
//            HttpServletRequest request,
//            Authentication auth
//    ) {
//
//        AjaxResult result = new AjaxResult();
//
//        User user = (User) auth.getPrincipal();
//        boolean superUser = user.getSuperUser();
//
//        if (!superUser) {
//            superUser = user.getUserProfile().getUserGroup().getCode().equals("dev");
//
//        }
//
////        List<Map<String, Object>> items = this.etctelListService.getUserList(superUser, group, keyword, username);
//
////        result.data = items;
//        return result;
//
//    }


    // 비상연락망 그룹 조회
    @GetMapping("/etctel_grp_list")
    public AjaxResult getEtctelGrpList(
            @RequestParam(value = "id") Integer id,
            HttpServletRequest request
    ){
//        List<Map<String,Object>> items = this.etctelListService.getEtctelGrpList(id);
        AjaxResult result = new AjaxResult();
//        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult addEtctelList(
            @RequestParam(value = "emcontno", required = false) String emcontno,
            @RequestParam(value="emconcomp", required=false) String comp,
            @RequestParam(value="emconper", required=false) String per,
            @RequestParam(value="emcontel", required=false) String tel,
            @RequestParam(value="indatem", required=false) Date indatem,
            @RequestParam(value="inuserid", required=false) String inuserid,
            @RequestParam(value="inusernm", required=false) String inusernm,
            @RequestParam(value="emconemail", required = false) String emconemail,
            @RequestParam(value="spworkcd", required = false) String spworkcd,
            @RequestParam(value="spcompcd", required = false) String spcompcd,
            @RequestParam(value="taskwork", required = false) String taskwork,
            @RequestParam(value="divinm", required = false) String divinm,
            @RequestParam(value="emconmno", required = false) String emconmno,
            HttpServletRequest request,
            Authentication auth) {

        AjaxResult result = new AjaxResult();

//         현재 사용자 정보 가져오기
        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());


        // DTO 생성 및 값 설정
        TB_RP980Dto tbRp980Dto = new TB_RP980Dto();
        tbRp980Dto.setEmcontno(emcontno);
        tbRp980Dto.setComp("대구");
        tbRp980Dto.setPer(per);
        tbRp980Dto.setTel(tel);
        tbRp980Dto.setIndatem(now);
        tbRp980Dto.setInuserid(String.valueOf(user.getId()));
        tbRp980Dto.setInusernm(user.getUsername());
        tbRp980Dto.setEmail(emconemail);
        tbRp980Dto.setWorkcd("001"); //관할지역코드
        tbRp980Dto.setCompcd("001");    // 발전산단코드
        tbRp980Dto.setTaskwork(taskwork);
        tbRp980Dto.setDivinm(divinm);
        tbRp980Dto.setMno(emconmno);

//         비상연락망 정보 저장 및 결과 조회
        List<Map<String, Object>> items = this.etctelListService.tb_rp980add(tbRp980Dto);

        // 여기서 추가된 비상연락망 정보를 처리하거나 결과에 따른 처리를 할 수 있음
        // 예를 들어, AjaxResult에 추가 정보를 설정할 수 있음
//        result.setData(items);
//        result.setSuccess(true); // 성공 여부 설정
        result.data = items;

        return result;
    }

    @PostMapping("/delete")
    public AjaxResult deleteEtctelList(@RequestParam("id") String id) {


        if(id != null){
            this.tp980Repository.deleteById(id);
        }

        AjaxResult result = new AjaxResult();

        return result;
    }






}
