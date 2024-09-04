package mes.app.actas_inspec;


import mes.app.UtilClass;
import mes.app.actas_inspec.service.InspecStaticService;
import mes.domain.model.AjaxResult;
import mes.domain.repository.actasRepository.TB_RP710Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inspec_statics")
public class InspecStaticController {


    @Autowired
    InspecStaticService inspecStaticService;

    @Autowired
    TB_RP710Repository tb_rp710Repository;


    @GetMapping("/RP710/read")
    public AjaxResult RP710List(@RequestParam(value = "searchfrdate", required = false) String searchfrdate,
                                @RequestParam(value = "searchtodate", required = false) String searchtodate,
                                @RequestParam(value = "searchfrQuarter", required = false) String searchfrQuarter,
                                @RequestParam(value = "searchtoQuarter", required = false) String searchtoQuarter,
                                @RequestParam(value = "searchYear", required = false) String searchYear,
                                @RequestParam(value = "searchfrHalfYear", required = false) String searchfrHalfYear,
                                @RequestParam(value = "searchtoHalfYear", required = false) String searchtoHalfYear,
                                @RequestParam(value = "searchfrYear", required = false) String searchfrYear,
                                @RequestParam(value = "searchtoYear", required = false) String searchtoYear,
                                @RequestParam(value = "searchType", required = true) String searchType
                                ){

        AjaxResult result = new AjaxResult();

        String startdate = null;
        String endDate = null;
        List<Map<String, Object>> items = new ArrayList<>();

        switch (searchType){
            //순회점검 월별 조회
            case "monthly":

                startdate = getFirstDate(searchfrdate);

                endDate = getLastDate(searchtodate);

                items = inspecStaticService.getRP710List(startdate, endDate);
            break;

            //순회점검 분기별 조회
            case "quarterly":

                startdate = getQuarterFirstDate(searchYear, searchfrQuarter);

                endDate = getQuarterEndDate(searchYear, searchtoQuarter);

            items = inspecStaticService.getRP710List(startdate, endDate);

            break;

            case "halfyearly":

                startdate = getHalfYearFirstDate(searchYear, searchfrHalfYear);

                endDate = getHalfYearEndDate(searchYear, searchtoHalfYear);

                items = inspecStaticService.getRP710List(startdate, endDate);
            break;


            case "yearly":
                startdate = searchfrYear + "0101";
                endDate   = searchtoYear + "1231";

                items = inspecStaticService.getRP710List(startdate, endDate);
                break;
        }


        System.out.println(startdate);
        System.out.println(endDate);


        result.success = true;
        result.data = items;
        return result;
    }
    
    @GetMapping("/RP720/read")
    public AjaxResult RP720List(@RequestParam(value = "searchfrdate", required = false) String searchfrdate,
                                @RequestParam(value = "searchtodate", required = false) String searchtodate,
                                @RequestParam(value = "searchfrQuarter", required = false) String searchfrQuarter,
                                @RequestParam(value = "searchtoQuarter", required = false) String searchtoQuarter,
                                @RequestParam(value = "searchYear", required = false) String searchYear,
                                @RequestParam(value = "searchfrHalfYear", required = false) String searchfrHalfYear,
                                @RequestParam(value = "searchtoHalfYear", required = false) String searchtoHalfYear,
                                @RequestParam(value = "searchfrYear", required = false) String searchfrYear,
                                @RequestParam(value = "searchtoYear", required = false) String searchtoYear,
                                @RequestParam(value = "searchType", required = true) String searchType){

        AjaxResult result = new AjaxResult();

        String startdate = null;
        String endDate = null;
        List<Map<String, Object>> items = new ArrayList<>();

        switch (searchType){
            //순회점검 월별 조회
            case "monthly":

                startdate = getFirstDate(searchfrdate);

                endDate = getLastDate(searchtodate);

                items = inspecStaticService.getRP720List(startdate, endDate);
                break;

            //순회점검 분기별 조회
            case "quarterly":

                startdate = getQuarterFirstDate(searchYear, searchfrQuarter);

                endDate = getQuarterEndDate(searchYear, searchtoQuarter);

                items = inspecStaticService.getRP720List(startdate, endDate);

                break;

            case "halfyearly":

                startdate = getHalfYearFirstDate(searchYear, searchfrHalfYear);

                endDate = getHalfYearEndDate(searchYear, searchtoHalfYear);

                items = inspecStaticService.getRP720List(startdate, endDate);
                break;


            case "yearly":
                startdate = searchfrYear + "0101";
                endDate   = searchtoYear + "1231";

                items = inspecStaticService.getRP720List(startdate, endDate);
                break;
        }


        System.out.println(startdate);
        System.out.println(endDate);


        result.success = true;
        result.data = items;
        return result;
    }

    @GetMapping("/RP750/read")
    public AjaxResult RP750List(@RequestParam(value = "searchfrdate", required = false) String searchfrdate,
                                @RequestParam(value = "searchtodate", required = false) String searchtodate,
                                @RequestParam(value = "searchfrQuarter", required = false) String searchfrQuarter,
                                @RequestParam(value = "searchtoQuarter", required = false) String searchtoQuarter,
                                @RequestParam(value = "searchYear", required = false) String searchYear,
                                @RequestParam(value = "searchfrHalfYear", required = false) String searchfrHalfYear,
                                @RequestParam(value = "searchtoHalfYear", required = false) String searchtoHalfYear,
                                @RequestParam(value = "searchfrYear", required = false) String searchfrYear,
                                @RequestParam(value = "searchtoYear", required = false) String searchtoYear,
                                @RequestParam(value = "searchType", required = true) String searchType
    ){

        AjaxResult result = new AjaxResult();

        String startdate = null;
        String endDate = null;
        List<Map<String, Object>> items = new ArrayList<>();

        switch (searchType){
            //순회점검 월별 조회
            case "monthly":

                startdate = getFirstDate(searchfrdate);

                endDate = getLastDate(searchtodate);

                items = inspecStaticService.getRP750List(startdate, endDate);
                break;

            //순회점검 분기별 조회
            case "quarterly":

                startdate = getQuarterFirstDate(searchYear, searchfrQuarter);

                endDate = getQuarterEndDate(searchYear, searchtoQuarter);

                items = inspecStaticService.getRP750List(startdate, endDate);

                break;

            case "halfyearly":

                startdate = getHalfYearFirstDate(searchYear, searchfrHalfYear);

                endDate = getHalfYearEndDate(searchYear, searchtoHalfYear);

                items = inspecStaticService.getRP750List(startdate, endDate);
                break;


            case "yearly":
                startdate = searchfrYear + "0101";
                endDate   = searchtoYear + "1231";

                items = inspecStaticService.getRP750List(startdate, endDate);
                break;
        }


        System.out.println(startdate);
        System.out.println(endDate);


        result.success = true;
        result.data = items;
        return result;
    }



    private String getHalfYearEndDate(String year, String halfyear){

        String endDate = "";

        endDate = switch (halfyear) {
            case "H1" -> year + "0630";
            case "H2" -> year + "1231";
            default -> endDate;
        };
        return endDate;
    }

    private String getHalfYearFirstDate(String year, String halfyear){

        String startdate = "";

        startdate = switch (halfyear) {
            case "H1" -> year + "0101";
            case "H2" -> year + "0701";
            default -> startdate;
        };

        return startdate;
    }

    private String getQuarterFirstDate(String year, String Quarter){

        String startdate = "";
        startdate = switch (Quarter) {
            case "Q1" -> year + "0101";
            case "Q2" -> year + "0401";
            case "Q3" -> year + "0701";
            case "Q4" -> year + "1001";
            default -> startdate;
        };

        return startdate;
    }

    private String getQuarterEndDate(String year, String Quarter){
        String endDate = "";
        endDate = switch (Quarter){
            case "Q1" -> year + "0331";
            case "Q2" -> year + "0630";
            case "Q3" -> year + "0930";
            case "Q4" -> year + "1231";
            default -> endDate;
        };
        return endDate;
    }

    //조회시작 날짜 구하기
    private String getFirstDate(String date){

        String dateValue;

        if(date.contains("-")){
            dateValue = date.replaceAll("-","") + "01";
        }else{
            dateValue = date + "01";
        }
        return dateValue;
    }

    //조회 종료날짜 구하기
    private String getLastDate(String date){
        String dateValue;

        if(date.contains("-")){
            dateValue = date.replaceAll("-", "");
        }else{
            dateValue = date;
        }
        dateValue = new UtilClass().getLastDay(dateValue);

        return dateValue;
    }

    @GetMapping("/distinctYears")
    public ResponseEntity<List<String>> getDistinctYears() {
        List<String> years = tb_rp710Repository.findDistinctYears();
        return ResponseEntity.ok(years);
    }
}
