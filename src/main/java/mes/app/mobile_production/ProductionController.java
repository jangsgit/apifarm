package mes.app.mobile_production;

import mes.app.mobile_production.service.ProductionService;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_DA006W_PK;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile_production")
public class ProductionController {
    @Autowired
    private ProductionService productionService;
    // 프로시저 리스트
    @GetMapping("/read_all")
    public AjaxResult productionList(@RequestParam(value = "search_startDate", required = false) String searchStartDate,
                                     @RequestParam(value = "search_endDate", required = false) String searchEndDate,
                                     @RequestParam(value = "search_product", required = false) String searchProduct,
                                     @RequestParam(value = "search_cltcd", required = false) String searchCltcd,
                                     Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = productionService.getUserInfo(username);
        String search_startDate = (searchStartDate).replaceAll("-","");
        String search_endDate = (searchEndDate).replaceAll("-","");

        Map<String, Object> searchLabels = new HashMap<>();
        searchLabels.put("search_spjangcd", (String) userInfo.get("spjangcd"));
        searchLabels.put("search_custcd", (String) userInfo.get("custcd"));
        searchLabels.put("search_startDate", search_startDate);
        searchLabels.put("search_endDate", search_endDate);
        searchLabels.put("search_product", searchProduct);
        searchLabels.put("search_cltcd", searchCltcd);
        List<Map<String, Object>> productList = productionService.getProductionList(searchLabels);

        productList.forEach(product -> {
            // 기존 값을 가져오기
            if(product.get("WFLAG01") != null) {
                String originalValue1 = product.get("WFLAG01").toString();
                if (originalValue1.length() > 2) {
                    originalValue1 = originalValue1.substring(2);
                    // 새로운 값으로 업데이트
                    Map<String, Object> newValue = productionService.getProcess(originalValue1);
                    // 맵에 업데이트된 값 넣기
                    product.put("WFLAG01", newValue.get("com_cnam"));
                }
            }

            if(product.get("WFLAG02") != null) {
                String originalValue2 = product.get("WFLAG02").toString();
                if (originalValue2.length() > 2) {
                    originalValue2 = originalValue2.substring(2);
                    Map<String, Object> newValue2 = productionService.getProcess(originalValue2);
                    product.put("WFLAG02", newValue2.get("com_cnam"));
                }
            }
            if(product.get("WFLAG03") != null) {
                String originalValue3 = product.get("WFLAG03").toString();
                if (originalValue3.length() > 2) {
                    originalValue3 = originalValue3.substring(2);
                    Map<String, Object> newValue3 = productionService.getProcess(originalValue3);
                    product.put("WFLAG03", newValue3.get("com_cnam"));
                }
            }
            // 날짜 형식 변환 (PRODDATE)
            if (product.containsKey("PRODDATE")) {
                String setupdt = (String) product.get("PRODDATE");
                if (setupdt != null && setupdt.length() == 8) {
                    String formattedDate = setupdt.substring(0, 4) + "-" + setupdt.substring(4, 6) + "-" + setupdt.substring(6, 8);
                    product.put("PRODDATE", formattedDate);
                }
            }
            // 날짜 형식 변환 (WORDT01)
            if (product.containsKey("WORDT01")) {
                String setupdt = (String) product.get("WORDT01");
                if (setupdt != null && setupdt.length() == 8) {
                    String formattedDate = setupdt.substring(0, 4) + "-" + setupdt.substring(4, 6) + "-" + setupdt.substring(6, 8);
                    product.put("WORDT01", formattedDate);
                }
            }
            // 날짜 형식 변환 (WORDT02)
            if (product.containsKey("WORDT02")) {
                String setupdt = (String) product.get("WORDT02");
                if (setupdt != null && setupdt.length() == 8) {
                    String formattedDate = setupdt.substring(0, 4) + "-" + setupdt.substring(4, 6) + "-" + setupdt.substring(6, 8);
                    product.put("WORDT02", formattedDate);
                }
            }
            // 날짜 형식 변환 (WORDT03)
            if (product.containsKey("WORDT03")) {
                String setupdt = (String) product.get("WORDT03");
                if (setupdt != null && setupdt.length() == 8) {
                    String formattedDate = setupdt.substring(0, 4) + "-" + setupdt.substring(4, 6) + "-" + setupdt.substring(6, 8);
                    product.put("WORDT03", formattedDate);
                }
            }
        });

        AjaxResult result = new AjaxResult();
        result.data = productList;
        return result;
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
