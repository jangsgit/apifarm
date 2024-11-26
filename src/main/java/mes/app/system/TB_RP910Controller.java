package mes.app.system;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mes.app.system.service.Tbrp910Service;
import mes.domain.entity.actasEntity.TB_RP910;
import mes.domain.repository.actasRepository.TB_RP910Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import mes.app.system.service.UserCodeService;
import mes.domain.entity.User;
import mes.domain.entity.UserCode;
import mes.domain.model.AjaxResult;
import mes.domain.repository.UserCodeRepository;


@RestController
@RequestMapping("/api/system/tbrp910")
public class TB_RP910Controller {


    @Autowired
    private UserCodeService codeService;

    @Autowired
    private Tbrp910Service tbrp910Service;

    @Autowired
    UserCodeRepository userCodeRepository;

    @Autowired
    TB_RP910Repository tb_rp910Repository;

    @GetMapping("/read")
    public AjaxResult getCodeList(
            @RequestParam("txtCode") String txtCode
    ) {

        List<Map<String, Object>> items = this.tbrp910Service.getCodeList(txtCode);
        AjaxResult result = new AjaxResult();

        result.data = items;
        return result;
    }

//    @GetMapping("/detail")
//    public AjaxResult getCode(@RequestParam("id") int id) {
//        Map<String, Object> item = this.codeService.getCode(id);
//
//        AjaxResult result = new AjaxResult();
//        result.data = item;
//        return result;
//    }

//        @PostMapping("/save")
//        public AjaxResult saveCode(
//                @RequestParam(value="itemcd", required=false) String itemcd,
//                @RequestParam("itemnm") String itemnm,
//    //            @RequestParam("itemcd") String itemcd,
//                @RequestParam(value="groupcd" , required=false) String groupcd,
//                @RequestParam("itemrm") String itemrm,
//                HttpServletRequest request,
//                Authentication auth) {
//
//            Timestamp now = new Timestamp(System.currentTimeMillis());
//            User user = (User) auth.getPrincipal();
//
//            TB_RP910 c = null;
//
//            if (itemcd == null) {
//                c = new TB_RP910();
//            } else {
//                c = this.tb_rp910Repository.getTbRp910ByItemcd(itemcd);
//            }
//            if (c == null) {
//                c = new TB_RP910();
//            }
//            if(itemcd == null){
//                itemcd +=1;
//            }
//
//            c.setGroupcd(groupcd);
//            c.setItemnm(itemnm);
//            c.setItemrm(itemrm);
//            c.setItemcd(itemcd);
//            c.setInuserid(String.valueOf(user.getId()));
//            c.setInusernm(user.getUsername());
//            c.setIndatem(now);
//
//
//            c = this.tb_rp910Repository.save(c);
//
//            AjaxResult result = new AjaxResult();
//            result.data = c;
//
//            return result;
//        }


    @PostMapping("/save")
    public AjaxResult saveCode(
            @RequestParam(value="itemcd", required=false) String itemcd,
            @RequestParam("itemnm") String itemnm,
            //            @RequestParam("itemcd") String itemcd,
            @RequestParam(value="groupcd" , required=false) String groupcd,
            @RequestParam("itemrm") String itemrm,
            HttpServletRequest request,
            Authentication auth) {

        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = (User) auth.getPrincipal();

        // 기존 데이터 조회
        TB_RP910 existingEntity = null;
        boolean isNew = false;


        // itemcd가 제공되면 기존 데이터 조회
        if (itemcd != null && !itemcd.isEmpty()) {
            existingEntity = tb_rp910Repository.getTbRp910ByItemcd(itemcd);
        }

        // 기존 데이터가 없으면 새로 생성
        if (existingEntity == null) {
            existingEntity = new TB_RP910();
            existingEntity.setItemcd(itemcd); // 새 데이터의 itemcd 설정
            isNew = true;

            existingEntity.setGroupcd(groupcd);
            existingEntity.setItemnm(itemnm);
            existingEntity.setItemrm(itemrm);
            existingEntity.setInuserid(String.valueOf(user.getId()));
            existingEntity.setInusernm(user.getUsername());
            existingEntity.setIndatem(now);

            existingEntity = this.tb_rp910Repository.save(existingEntity);

        }

        // 데이터 변경 여부 확인 및 업데이트
        boolean isUpdated = false;
        if (!itemnm.equals(existingEntity.getItemnm()) ||
                (groupcd != null && !groupcd.equals(existingEntity.getGroupcd())) ||
                !itemrm.equals(existingEntity.getItemrm())) {

            existingEntity.setGroupcd(groupcd);
            existingEntity.setItemnm(itemnm);
            existingEntity.setItemrm(itemrm);
            existingEntity.setInuserid(String.valueOf(user.getId()));
            existingEntity.setInusernm(user.getUsername());
            existingEntity.setIndatem(now);

            // 데이터베이스에 저장 (삽입 또는 업데이트)
            tb_rp910Repository.save(existingEntity);
            isUpdated = true;
        }

        // 결과 반환
        AjaxResult result = new AjaxResult();
        if (isNew) {
            result.message = "새로운 데이터가 성공적으로 저장되었습니다.";
        } else if (isUpdated) {
            result.message = "데이터가 성공적으로 업데이트 되었습니다.";
        } else {
            result.message = "변경된 데이터가 없습니다.";
        }
        result.data=existingEntity;

        return result;
    }





//    @PostMapping("/delete")
//    public AjaxResult deleteCode(@RequestBody Map<String, Object> payload) {
//        AjaxResult result = new AjaxResult();
//        try {
//            String itemcd = (String) payload.get("id");
//            tb_rp910Repository.deleteById(itemcd);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//        return result;
//    }


    @PostMapping("/delete")
    @Transactional
    public AjaxResult deleteCode(@RequestParam(value = "itemcds") List<String> itemcds) {
        AjaxResult result = new AjaxResult();

        try {
            for (String itemcd : itemcds) {
                tb_rp910Repository.deleteById(itemcd);
            }
            result.success = true; // 성공 플래그 설정
            result.message = "삭제되었습니다"; // 성공 메시지 설정
        } catch (Exception e) {
            result.success = false; // 실패 플래그 설정
            result.message = "삭제 실패: " + e.getMessage(); // 실패 메시지 설정
            e.printStackTrace();
            throw e; // 트랜잭션 롤백을 위해 예외 다시 던지기
        }
        return result;
    }
}




