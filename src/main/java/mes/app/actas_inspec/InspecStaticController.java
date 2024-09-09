package mes.app.actas_inspec;


import mes.app.UtilClass;
import mes.app.actas_inspec.service.InspecStaticService;
import mes.domain.model.AjaxResult;
import mes.domain.model.AjaxReturn;
import mes.domain.repository.actasRepository.TB_RP710Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/inspec_statics")
public class InspecStaticController {


    @Autowired
    InspecStaticService inspecStaticService;

    @Autowired
    TB_RP710Repository tb_rp710Repository;


    @GetMapping("/read")
    public AjaxReturn ReadList(@RequestParam(value = "searchfrdate", required = false) String searchfrdate,
                               @RequestParam(value = "searchtodate", required = false) String searchtodate,
                               @RequestParam(value = "searchfrQuarter", required = false) String searchfrQuarter,
                               @RequestParam(value = "searchtoQuarter", required = false) String searchtoQuarter,
                               @RequestParam(value = "searchYear", required = false) String searchYear,
                               @RequestParam(value = "searchfrHalfYear", required = false) String searchfrHalfYear,
                               @RequestParam(value = "searchtoHalfYear", required = false) String searchtoHalfYear,
                               @RequestParam(value = "searchfrYear", required = false) String searchfrYear,
                               @RequestParam(value = "searchtoYear", required = false) String searchtoYear,
                               @RequestParam(value = "startHour", required = false) String startHour,
                               @RequestParam(value = "endHour", required = false) String endHour,
                               @RequestParam(value = "searchType", required = true) String searchType,
                               @RequestParam(value = "wm_flag", required = true) String wm_flag,
                               @RequestParam(value = "read_flag", required = true) String read_flag

                               ) throws ParseException {

        AjaxReturn result = new AjaxReturn();

        String startdate = null;
        String endDate = null;
        List<Map<String, Object>> items = new ArrayList<>();




        switch (searchType){

            case "hourly":
                if(searchfrdate.contains("-")){
                    startdate = searchfrdate.replaceAll("-","");
                }else{
                    startdate = searchfrdate;
                }

                if(wm_flag.equals("wm_inspec_month_list")){
                    items = inspecStaticService.getRP710List(startdate, "", startHour, endHour);
                    result.subData = this.getRmaTeGraphData_Hourly(items);
                }else if(wm_flag.equals("wm_hap_input")){
                    items = inspecStaticService.getRP720List(startdate, "", startHour, endHour);
                    result.subData = this.getRmaTeGraphData_Hourly(items);
                }else if(wm_flag.equals("wm_elecsafe_input")){
                    items = inspecStaticService.getRP750List(startdate, "", startHour, endHour);
                    result.subData = this.getRmaTeGraphData_Hourly(items);
                }
                result.data = items;


                break;

            //순회점검 월별 조회
            case "monthly":
                //시작날짜 구함
                startdate = getFirstDate(searchfrdate);
                //종료날짜 구함
                endDate = getLastDate(searchtodate);

                if(read_flag.equals("Original")){
                    //DB에서 데이터 조회해옴
                    items = getItems(wm_flag, startdate, endDate);
                    //dto에 담아서 뷰로 ㄱㄱ
                    result.data = items;
                    result.subData = this.getRmaTeGraphData_Month(items);
                }else if(read_flag.equals("YoY")){

                    result.data = this.getRmaTeGraphData_Month(getItems(wm_flag, startdate, endDate));
                    result.subData = this.getRmaTeGraphData_Month(getComparisonItems(wm_flag, startdate, endDate, read_flag));
                }
                break;

            //순회점검 분기별 조회
            case "quarterly":

                //시작날짜 구함
                startdate = getQuarterFirstDate(searchYear, searchfrQuarter);
                //종료날짜 구함
                endDate = getQuarterEndDate(searchYear, searchtoQuarter);
                //DB에서 데이터 조회해옴
                items = getItems(wm_flag, startdate, endDate);
                //dto에 담아서 뷰로 ㄱㄱ
                result.data = items;
                result.subData = this.getRmaTeGraphData_Quarter(items);

                break;

            case "halfyearly":

                startdate = getHalfYearFirstDate(searchYear, searchfrHalfYear);

                endDate = getHalfYearEndDate(searchYear, searchtoHalfYear);

                items = getItems(wm_flag, startdate, endDate);

                result.data = items;
                result.subData = this.getRmaTeGraphData_HalfYear(items);

                break;


            case "yearly":

                startdate = searchfrYear + "0101";

                endDate   = searchtoYear + "1231";

                items = getItems(wm_flag, startdate, endDate);

                result.data = items;
                result.subData = this.getRmaTeGraphData_Yearly(items);

                break;
        }


        System.out.println(startdate);
        System.out.println(endDate);


        result.status = true;
        return result;
    }

    private List<Map<String, Object>> getItems(String wm_flag, String startdate, String endDate){

        List<Map<String, Object>> items = new ArrayList<>();

        if(wm_flag.equals("wm_inspec_month_list")){
            items = inspecStaticService.getRP710List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_hap_input")){
            items = inspecStaticService.getRP720List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_elecsafe_input")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_emergency_plan")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_emergency_result")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_safety_health")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_inspec_field_report")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }

        return items;
    }

    private List<Map<String, Object>> getComparisonItems(String wm_flag, String startdate, String endDate, String Comparison){

        List<Map<String, Object>> items = new ArrayList<>();

        if(wm_flag.equals("wm_inspec_month_list")){
            items = inspecStaticService.getRP710ComparisonList(startdate, endDate, null, null, Comparison);
        }else if(wm_flag.equals("wm_hap_input")){
            items = inspecStaticService.getRP720List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_elecsafe_input")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_emergency_plan")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_emergency_result")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_safety_health")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }else if(wm_flag.equals("wm_inspec_field_report")){
            items = inspecStaticService.getRP750List(startdate, endDate, null, null);
        }

        return items;
    }


    private Map<String, Integer> getRmaTeGraphData_Hourly(List<Map<String, Object>> items) {

        Map<String, Integer> countByHour = new HashMap<>();

        for (Map<String, Object> item : items) {
            // Get the Timestamp directly from the item
            java.sql.Timestamp timestamp = (java.sql.Timestamp) item.get("indatem");

            if (timestamp != null) {
                // Extract the hour from the Timestamp using Calendar
                Calendar cal = Calendar.getInstance();
                cal.setTime(timestamp);
                int hour = cal.get(Calendar.HOUR_OF_DAY); // 24-hour format (00-23)

                // Update the count for the specific hour
                String hourStr = String.format("%02d", hour) + "시"; // Format the hour to 2 digits (e.g., "01", "23")
                countByHour.put(hourStr, countByHour.getOrDefault(hourStr, 0) + 1);
            }
        }
        return countByHour;
    }



    private Map<String, Integer> getRmaTeGraphData_Yearly(List<Map<String, Object>> items) throws ParseException {

        Map<String, Integer> countByYear = new HashMap<>();

        // Input format for "20240827" (yyyyMMdd)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        // Output format for year extraction
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        for (Map<String, Object> item : items) {
            String dateStr = (String) item.get("checkdt");

            try {
                // Parse the date string from "yyyyMMdd" format
                Date date = inputFormat.parse(dateStr);
                // Extract year from the date
                String year = yearFormat.format(date);

                // Update the count for the specific year
                countByYear.put(year, countByYear.getOrDefault(year, 0) + 1);

            } catch (ParseException e) {
                // Handle parsing error if the date is unparseable
                System.out.println("Unparseable date: " + dateStr);
            }
        }
        return countByYear;
    }


    private Map<String, Integer> getRmaTeGraphData_HalfYear(List<Map<String, Object>> items) throws ParseException {

        Map<String, Integer> countByHalfYear = new HashMap<>();

        // Input format for "20240827" (yyyyMMdd)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        // Output format for year extraction
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        for (Map<String, Object> item : items) {
            String dateStr = (String) item.get("checkdt");

            try {
                // Parse the date string from "yyyyMMdd" format
                Date date = inputFormat.parse(dateStr);
                // Extract year and month from the date
                String year = yearFormat.format(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based, so add 1

                // Determine the half-year based on the month
                String halfYear;
                if (month >= 1 && month <= 6) {
                    halfYear = "상반기"; // 1st Half (Jan - Jun)
                } else {
                    halfYear = "하반기"; // 2nd Half (Jul - Dec)
                }

                // Create the key as "YYYY-HX"
                String yearHalf = year + "-" + halfYear;

                // Update the count for the specific half-year
                countByHalfYear.put(yearHalf, countByHalfYear.getOrDefault(yearHalf, 0) + 1);

            } catch (ParseException e) {
                // Handle parsing error if the date is unparseable
                System.out.println("Unparseable date: " + dateStr);
            }
        }
        return countByHalfYear;
    }


    private Map<String, Integer> getRmaTeGraphData_Quarter(List<Map<String, Object>> items) throws ParseException {

        Map<String, Integer> countByQuarter = new HashMap<>();

        // Input format for "20240827" (yyyyMMdd)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        // Output format for year extraction
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        for (Map<String, Object> item : items) {
            String dateStr = (String) item.get("checkdt");

            try {
                // Parse the date string from "yyyyMMdd" format
                Date date = inputFormat.parse(dateStr);
                // Extract year and month from the date
                String year = yearFormat.format(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based, so add 1

                // Determine the quarter based on the month
                String quarter;
                if (month >= 1 && month <= 3) {
                    quarter = "Q1"; // 1st Quarter (Jan - Mar)
                } else if (month >= 4 && month <= 6) {
                    quarter = "Q2"; // 2nd Quarter (Apr - Jun)
                } else if (month >= 7 && month <= 9) {
                    quarter = "Q3"; // 3rd Quarter (Jul - Sep)
                } else {
                    quarter = "Q4"; // 4th Quarter (Oct - Dec)
                }

                // Create the key as "YYYY-QX"
                String yearQuarter = year + "-" + quarter;

                // Update the count for the specific quarter
                countByQuarter.put(yearQuarter, countByQuarter.getOrDefault(yearQuarter, 0) + 1);

            } catch (ParseException e) {
                // Handle parsing error if the date is unparseable
                System.out.println("Unparseable date: " + dateStr);
            }
        }
        return countByQuarter;
    }

    private Map<String, Integer> getRmaTeGraphData_Month(List<Map<String, Object>> items) throws ParseException {

        Map<String, Integer> countByMonth = new HashMap<>();

        // Input format for "20240827" (yyyyMMdd)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        // Output format for "yyyy-MM" (year and month with hyphen)
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

        for (Map<String, Object> item : items) {
            String hourStr = (String) item.get("checkdt");

            try {
                // Parse the hour string from "yyyyMMdd" format
                Date hourDate = inputFormat.parse(hourStr);
                // Format the date as "yyyy-MM" (with hyphen)
                String yearMonth = yearMonthFormat.format(hourDate);

                // Update the count for the specific month
                countByMonth.put(yearMonth, countByMonth.getOrDefault(yearMonth, 0) + 1);
            } catch (ParseException e) {
                // Handle parsing error if the date is unparseable
                System.out.println("Unparseable date: " + hourStr);
            }
        }
        return countByMonth;
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
