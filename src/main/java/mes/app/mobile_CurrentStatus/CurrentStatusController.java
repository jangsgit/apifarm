package mes.app.mobile_CurrentStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mes.app.mobile_CurrentStatus.service.CurrentStatusService;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/CurrentStatus")
public class CurrentStatusController {

    @Autowired
    public CurrentStatusService currentStatusService;

    @GetMapping("/read")
    public ResponseEntity<?> getCurrentStatus(
            Authentication auth,
            @RequestParam(required = false) String cltnm,
            @RequestParam(required = false) String pname,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            // 사용자 정보 추출
            User user = (User) auth.getPrincipal();
            String username = user.getUsername();
            String spjangCd = user.getSpjangcd();

            // custCd 조회
            String custCd = currentStatusService.getCustCdByUsername(username);

            // 데이터 조회
            List<Map<String, Object>> data = currentStatusService.getCurrentStatus(
                    custCd, spjangCd, cltnm, pname, startDate, endDate
            );

          /*  // 로그 출력 (최대 2건만 출력)
            if (data != null && !data.isEmpty()) {
                int maxLogCount = 10;
                List<Map<String, Object>> limitedData = data.size() > maxLogCount ? data.subList(0, maxLogCount) : data;

                log.info("조회된 데이터 (총 {}건, 최대 {}건 출력): {}", data.size(), maxLogCount,
                        new ObjectMapper().writeValueAsString(limitedData));
            } else {
                log.info("조회된 데이터가 없습니다.");
            }*/

            // 데이터 반환
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "알 수 없는 오류가 발생했습니다."));
        }
    }



}
