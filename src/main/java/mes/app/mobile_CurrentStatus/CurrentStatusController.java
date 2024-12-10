package mes.app.mobile_CurrentStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mes.app.mobile_CurrentStatus.service.CurrentStatusService;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/CurrentStatus")
public class CurrentStatusController {

    @Autowired
    public CurrentStatusService currentStatusService;

   /* @GetMapping("/read")
    public AjaxResult read(Authentication auth) {
        log.info("출고 형황 들어옴");
        AjaxResult result = new AjaxResult();

        try {
            User user = (User) auth.getPrincipal();
            String spjangCd  = user.getSpjangcd();

            String username = auth.getName();

            String custCd = currentStatusService.getCustCdByUsername(username);

            // 데이터 조회
            List<Map<String, Object>> data = currentStatusService.getCurrentStatus(custCd, spjangCd);

            // JSON 형태로 출력
            log.info("조회된 데이터: {}", new ObjectMapper().writeValueAsString(data));
            result.success = true;
            result.data = data; // 데이터 반환
        } catch (Exception e) {
            log.error("에러 발생", e);
            result.success = false;
            result.message = "알 수 없는 오류가 발생했습니다.";
        }

        return result;
    }*/

    @GetMapping("/read")
    public AjaxResult read(Authentication auth) {
        log.info("출고 현황 API 호출");
        AjaxResult result = new AjaxResult();

        try {
            // 사용자 정보 추출
            User user = (User) auth.getPrincipal();
            String username = user.getUsername();
            String spjangCd = user.getSpjangcd();

            // custCd 조회
            String custCd = currentStatusService.getCustCdByUsername(username);

            // 데이터 조회
            List<Map<String, Object>> data = currentStatusService.getCurrentStatus(custCd, spjangCd);

            // JSON 형태로 데이터 출력
            if (data != null && !data.isEmpty()) {
                log.info("조회된 데이터: {}", new ObjectMapper().writeValueAsString(data));
            } else {
                log.info("조회된 데이터가 없습니다.");
            }

            result.success = true;
            result.data = data; // 데이터 반환
        } catch (Exception e) {
            log.error("에러 발생", e);
            result.success = false;
            result.message = "알 수 없는 오류가 발생했습니다.";
        }

        return result;
    }



}
