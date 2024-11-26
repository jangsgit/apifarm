package mes.app.actas_inspec;


import mes.app.UtilClass;
import mes.app.actas_inspec.service.InspecReportService;
import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.model.AjaxResult;
import mes.domain.repository.actasRepository.TB_RP710Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportEvents")
public class InspecReportController {

    @Autowired
    TB_RP710Repository tb_rp710Repository;

    @Autowired
    InspecReportService inspecReportService;




    @GetMapping("/read")
    public AjaxResult getInspecList(@RequestParam(value = "date") String date){
        AjaxResult result = new AjaxResult();

        List<Map<String, Object>> items = this.inspecReportService.getInspecList(date);

        result.data = items;
        return result;
    }

    @GetMapping("/MonthAllRead")
    public AjaxResult getInspecAllList(@RequestParam(value = "date") String date){
        AjaxResult result = new AjaxResult();


        String frdate = "";
        String todate = "";

        if(date.contains("-")){
             frdate = date.substring(0, 8) + "01";
             todate = new UtilClass().getLastDay(date.substring(0,8).replaceAll("-", ""));
        }
        else{
            frdate = date.substring(0,6) + "01";
            todate = new UtilClass().getLastDay(date);
        }

        List<Map<String, Object>> items = this.inspecReportService.getInspecMonthList(frdate.replaceAll("-",""), todate.replaceAll("-", ""));

        result.data = items;
        return result;
    }

    @GetMapping("Inspection/status")
    public AjaxResult getInspecionStatus(@RequestParam (value = "year") String year,
                                         @RequestParam (value = "month") String month){

        AjaxResult result = new AjaxResult();

        if(month.length() < 2){
            month = "0" + month;
        }

        String day = year + month;

        String dayValue = new UtilClass().getLastDay(day);

        String frdate = day + "01";

        List<Map<String, Object>> CountList = inspecReportService.getStatusList(frdate, dayValue.replaceAll("-",""));

        result.success = true;
        result.data = CountList;

        return result;
    }


    @GetMapping("/Calendar")
    public AjaxResult getCalendar() {

        AjaxResult result = new AjaxResult();


        //List<TB_RP710> rp710List =  tb_rp710Repository.findCountGroupByCheckdt();
        List<Map<String, Object>> rp710List =  inspecReportService.getCalenderList();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map<String, Object> item : rp710List) {
            String checkdt = (String) item.get("checkdt");
            try {
                // 원래의 날짜 형식을 Date 객체로 변환
                String formattedDate = outputFormat.format(inputFormat.parse(checkdt));
                // 변환된 날짜 형식을 다시 Map에 저장
                item.put("checkdt", formattedDate);
                //item.put("talbename", "순회점검일지");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        result.success = true;
        result.data = rp710List;

        return result;
    }
}
