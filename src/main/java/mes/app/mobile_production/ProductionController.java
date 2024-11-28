package mes.app.mobile_production;

import mes.app.mobile_production.service.ProductionService;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile_production")
public class ProductionController {
    @Autowired
    private ProductionService productionService;

    @GetMapping("/read_all")
    public AjaxResult productionList(@RequestParam(value = "search_startDate", required = false) String searchStartDate,
                                     @RequestParam(value = "search_endDate", required = false) String searchEndDate,
                                     @RequestParam(value = "search_product", required = false) String searchRemark,
                                     @RequestParam(value = "search_cltcd", required = false) String searchOrdfalg,
                                     Authentication auth) {

        Map<String, Object> searchLabels = new HashMap<>();
        searchLabels.put("search_startDate", searchStartDate);
        searchLabels.put("search_endDate", searchEndDate);
        searchLabels.put("search_product", searchOrdfalg);
        searchLabels.put("search_cltcd", searchOrdfalg);
        List<Map<String, Object>> productList = productionService.getProductionList(searchLabels);

        AjaxResult result = new AjaxResult();
        result.data = productList;
        return result;
    }
}
