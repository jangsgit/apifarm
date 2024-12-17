package mes.app.mobile_production;

import mes.app.mobile_production.service.ProductionService;
import mes.domain.entity.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import mes.domain.entity.actasEntity.TB_DA006W_PK;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/mobile_production")
public class ProductionController {
    @Autowired
    private ProductionService productionService;

    @GetMapping("/flist02")  // 메인컨트롤상태
    public AjaxResult productionList(@RequestParam(value = "search", required = false) String searchStartDate,
                                     Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = productionService.getUserInfo(username);
        String search_startDate = (searchStartDate).replaceAll("-","");

        // 제어모드 상태 API URL
        String url01 = "http://10.141.8.162/api/Control/GetRunControlMode";
        // 장치상태 API URL
        String url02 = "http://10.141.8.162/api/Control/GetEquipState";
        // 센서값 API URL
        String url03 = "http://10.141.8.162/api/Control/ReadMiniFarmSensorValues";

        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        // 결과 통합
        Map<String, Object> result = new HashMap<>();

        // HTTP 헤더 설정 (예: JSON 데이터 전송)
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "*/*");
        // HttpEntity: 헤더만 담아 전송
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            // 제어모드 상태 API 호출 및 응답 처리
            ResponseEntity<String> response01 = restTemplate.exchange(
                    url01,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            // 장치상태 상태 API 호출 및 응답 처리
            ResponseEntity<String> response02 = restTemplate.exchange(
                    url02,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            // 센서값 API 호출 및 응답 처리
            ResponseEntity<String> response03 = restTemplate.exchange(
                    url03,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            result.put("api1", response01);
            result.put("api2", response02);
            result.put("api3", response03);

            // 응답 출력
//            System.out.println("Response Status: " + response01.getStatusCode());
//            System.out.println("Response01 Body: " + response01.getBody());

        } catch (Exception e) {
            // 예외 처리
            System.err.println("Error occurred while calling the API: " + e.getMessage());
        }

        AjaxResult resultmodel = new AjaxResult();
        resultmodel.data = result;
//        System.out.println("result: " + result);
        // 결과 반환
        return resultmodel;
    }


    @GetMapping("/flist03")  // 머슈룸정보
    public AjaxResult FarmList03(@RequestParam(value = "search", required = false) String searchStartDate,
                                 Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = productionService.getUserInfo(username);
        String search_startDate = (searchStartDate).replaceAll("-","");

        // 운영정보 API URL
        String url01 = "http://10.141.8.162/api/MushroomCtrlCondition";

        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        // 결과 통합
        Map<String, Object> result = new HashMap<>();

        // HTTP 헤더 설정 (예: JSON 데이터 전송)
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "*/*");
        // HttpEntity: 헤더만 담아 전송
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            // 머슈룸 API 호출 및 응답 처리
            ResponseEntity<String> response01 = restTemplate.exchange(
                    url01,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            result.put("api1", response01);

            // 응답 출력
//            System.out.println("Response Status: " + response01.getStatusCode());
//            System.out.println("Response01 Body: " + response01.getBody());

        } catch (Exception e) {
            // 예외 처리
            System.err.println("Error occurred while calling the API: " + e.getMessage());
        }

        AjaxResult resultmodel = new AjaxResult();
        resultmodel.data = result;
//        System.out.println("result: " + result);
        // 결과 반환
        return resultmodel;
    }

    @GetMapping("/flist04")  // 운영정보
    public AjaxResult FarmList04(@RequestParam(value = "search", required = false) String searchStartDate,
                                     Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = productionService.getUserInfo(username);
        String search_startDate = (searchStartDate).replaceAll("-","");

        // 운영정보 API URL
        String url01 = "http://10.141.8.162/api/RunControlInfo";

        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        // 결과 통합
        Map<String, Object> result = new HashMap<>();

        // HTTP 헤더 설정 (예: JSON 데이터 전송)
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "*/*");
        // HttpEntity: 헤더만 담아 전송
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            // 운영정보 API 호출 및 응답 처리
            ResponseEntity<String> response01 = restTemplate.exchange(
                    url01,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            result.put("api1", response01);

            // 응답 출력
//            System.out.println("Response Status: " + response01.getStatusCode());
//            System.out.println("Response01 Body: " + response01.getBody());

        } catch (Exception e) {
            // 예외 처리
            System.err.println("Error occurred while calling the API: " + e.getMessage());
        }

        AjaxResult resultmodel = new AjaxResult();
        resultmodel.data = result;
//        System.out.println("result: " + result);
        // 결과 반환
        return resultmodel;
    }



    @GetMapping("/flist05")  // 스케줄정보
    public AjaxResult FarmList05(@RequestParam(value = "search", required = false) String searchStartDate,
                                 Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = productionService.getUserInfo(username);
        String search_startDate = (searchStartDate).replaceAll("-","");

        // 스케줄정보 API URL
        String url01 = "http://10.141.8.162/api/Schedule";

        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        // 결과 통합
        Map<String, Object> result = new HashMap<>();

        // HTTP 헤더 설정 (예: JSON 데이터 전송)
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "*/*");
        // HttpEntity: 헤더만 담아 전송
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            // 스케줄 API 호출 및 응답 처리
            ResponseEntity<String> response01 = restTemplate.exchange(
                    url01,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            result.put("api1", response01);

            // 응답 출력
//            System.out.println("Response Status: " + response01.getStatusCode());
//            System.out.println("Response01 Body: " + response01.getBody());

        } catch (Exception e) {
            // 예외 처리
            System.err.println("Error occurred while calling the API: " + e.getMessage());
        }

        AjaxResult resultmodel = new AjaxResult();
        resultmodel.data = result;
//        System.out.println("result: " + result);
        // 결과 반환
        return resultmodel;
    }


    // 작업이력 팝업 데이터
    @GetMapping("/read_work")
    public AjaxResult workList(@RequestParam(value = "wono", required = false) String wono,
                                     Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = productionService.getUserInfo(username);

        Map<String, Object> searchLabels = new HashMap<>();
        searchLabels.put("search_spjangcd", (String) userInfo.get("spjangcd"));
        searchLabels.put("search_custcd", (String) userInfo.get("custcd"));
        searchLabels.put("wono", wono);
        List<Map<String, Object>> productList = productionService.getWorkList(searchLabels);
        productList.forEach(product -> {
            // 기존 값을 가져오기
            if(product.get("wflag") != null) {
                String originalValue1 = product.get("wflag").toString();
                if (originalValue1.length() > 2) {
                    originalValue1 = originalValue1.substring(2);
                    // 새로운 값으로 업데이트
                    Map<String, Object> newValue = productionService.getProcess(originalValue1);
                    // 맵에 업데이트된 값 넣기
                    product.put("wflag", newValue.get("com_cnam"));
                }
            }

            // 날짜 형식 변환 (wtrdt)
            if (product.containsKey("wtrdt")) {
                String setupdt = (String) product.get("wtrdt");
                if (setupdt != null && setupdt.length() == 8) {
                    String formattedDate = setupdt.substring(0, 4) + "-" + setupdt.substring(4, 6) + "-" + setupdt.substring(6, 8);
                    product.put("wtrdt", formattedDate);
                }
            }
        });
        AjaxResult result = new AjaxResult();
        result.data = productList;
        return result;
    }
    // 거래처 검색
    @GetMapping("/search_cltcd")
    public AjaxResult searchCltcd(@RequestParam(value = "search_cltnm", required = false) String search_cltnm,
                               Authentication auth) {

        List<Map<String, Object>> productList = productionService.searchCltcd(search_cltnm);

        AjaxResult result = new AjaxResult();
        result.data = productList;
        return result;
    }
    // 품목 검색
    @GetMapping("/search_product")
    public AjaxResult searchProduct(@RequestParam(value = "search_productnm", required = false) String search_product,
                               Authentication auth) {

        List<Map<String, Object>> productList = productionService.searchProduct(search_product);

        AjaxResult result = new AjaxResult();
        result.data = productList;
        return result;
    }
    // 그리드 리스트
    @GetMapping("/todayGrid")
    public AjaxResult searchTodayGrid(@RequestParam(value = "search_startDate", required = false) String searchStartDate,
                                      @RequestParam(value = "search_cltcd", required = false) String searchCltcd,
                                      @RequestParam(value = "search_product", required = false) String searchPcode,
                                      Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = productionService.getUserInfo(username);
        String search_startDate = (searchStartDate).replaceAll("-","");

        Map<String, Object> searchLabels = new HashMap<>();
        searchLabels.put("search_spjangcd", (String) userInfo.get("spjangcd"));
        searchLabels.put("search_custcd", (String) userInfo.get("custcd"));
        searchLabels.put("search_startDate", search_startDate);
        searchLabels.put("search_cltcd", searchCltcd);
        searchLabels.put("search_pcode", searchPcode);

        List<Map<String, Object>> productList = productionService.searchTodayGrid(searchLabels);

        AjaxResult result = new AjaxResult();
        result.data = productList;
        return result;
    }
}
