package mes.app.cost;

import mes.app.cost.service.GasBillService;
import mes.domain.model.AjaxResult;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cost/gas_bill")
public class GasBillController {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    GasBillService gasBillService;

    @GetMapping("/read")
    public AjaxResult getGasBillList(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {

        List<Map<String,Object>> items = new ArrayList<>();

        if (startDate == null){
            startDate = "";
        }
        if (endDate == null){
            endDate = "";
        }

        items = this.gasBillService.getMonthlyUsageSummary(startDate,endDate);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

}
