package mes.app.operate;

import mes.app.operate.service.TicketService;
import mes.config.Settings;
import mes.domain.model.AjaxResult;
import mes.domain.repository.actasRepository.TB_RP820Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operate/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TB_RP820Repository tb_rp820Repository;

    @Autowired
    Settings settings;

    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate,
                              @RequestParam(value = "searchtketnm", required = false) String searchtketnm) {

        if (searchtketnm == null) {
            searchtketnm = "";
        }

        if (startDate == null) {
            startDate = "";
        }

        if (endDate == null) {
            endDate = "";
        }

        String c_startDate = startDate.replaceAll("-", "");
        String c_endDate = endDate.replaceAll("-", "");



        AjaxResult result = new AjaxResult();


        return result;
    }

    @GetMapping("/requesterinfo")
    public AjaxResult getRequesterInfo(@RequestParam(value = "userid", required = false) String userid) {

        AjaxResult result = new AjaxResult();

        Map<String, Object> item = this.ticketService.getRequesterInfo(userid);

        result.data = item;
        return result;
    }

    @GetMapping("/ktlist")
    public AjaxResult getKtList() {

        AjaxResult result = new AjaxResult();

        List<Map<String, Object>> items = this.ticketService.getKtList();

        result.data = items;
        return result;
    }

}
