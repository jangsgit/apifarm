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
                               @RequestParam(value = "searchfrMonth", required = false) String searchfrMonth,
                               @RequestParam(value = "searchtoMonth", required = false) String searchtoMonth,
                               @RequestParam(value = "searchType", required = true) String searchType,
                               @RequestParam(value = "wm_flag", required = true) String wm_flag

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


                //DB에서 데이터 조회해옴
                items = getItems(wm_flag, startdate, endDate);
                //dto에 담아서 뷰로 ㄱㄱ
                result.data = items;
                result.subData = this.getRmaTeGraphData_Month(items);

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

            case "YoY":
                startdate = searchfrYear + "0101";
                endDate   = searchfrYear + "1231";


                items = getItems(wm_flag, startdate, endDate);
                result.data = getRmaTeGraphData_YoY(items, searchfrYear);

                startdate = searchtoYear + "0101";
                endDate   = searchtoYear + "1231";

                items = getItems(wm_flag, startdate, endDate);
                result.subData = getRmaTeGraphData_YoY(items, searchtoYear);



                break;

            case "QoQ":
                startdate = searchfrYear + "0101";
                endDate   = searchfrYear + "1231";

                items = getItems(wm_flag, startdate, endDate);
                result.data = getRmaTeGraphData_QoQ(items, searchfrYear);

                startdate = searchtoYear + "0101";
                endDate   = searchtoYear + "1231";

                items = getItems(wm_flag, startdate, endDate);
                result.subData = getRmaTeGraphData_QoQ(items, searchtoYear);

                break;

            case "MoM":
                startdate = searchfrYear + searchfrMonth.replace("월","") +  "01";

                String MonthOfLastDay = new UtilClass().getLastDay(searchtoYear + searchtoMonth.replaceAll("월", ""));

                //endDate   = searchtoYear + searchtoMonth.replace("월", "") +  MonthOfLastDay;

                endDate = MonthOfLastDay.replaceAll("-", "");


                items = getItems(wm_flag, startdate, endDate.replaceAll("-",""));

                Map<String, Map<String, Integer>> data = getRmaTeGraphData_MoM(items, startdate, endDate.replaceAll("-", ""));
                Map<String, Integer> mergedItem = new TreeMap<>();

                for(Map<String, Integer> innerMap : data.values()){
                    for(Map.Entry<String, Integer> innerEntry  : innerMap.entrySet()){
                        String innerKey = innerEntry.getKey();
                        Integer value = innerEntry.getValue();

                        mergedItem.merge(innerKey, value, Integer::sum);

                    }
                }
                result.data = mergedItem;

                break;

            case "YTD":

                startdate = searchfrYear + "0101";
                endDate   = searchfrYear + "1231";

                items = getItems(wm_flag, startdate, endDate);
                result.data = getRmaTeGraphData_YTD(items, searchfrYear);

                startdate = searchtoYear + "0101";
                endDate   = searchtoYear + "1231";

                items = getItems(wm_flag, startdate, endDate);
                result.subData = getRmaTeGraphData_YTD(items, searchtoYear);

            break;

        }

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


    private Map<String, Map<String, Integer>> getRmaTeGraphData_MoM(List<Map<String, Object>> items, String searchfrDate, String searchtoDate) throws ParseException {

        // TreeMap을 사용하여 연도를 기준으로 자동 정렬되도록 설정
        Map<String, Map<String, Integer>> countByYearMonth = new TreeMap<>();

        // Input format for "yyyyMMdd" (ex: 20230101)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        // Output format for "yyyy-MM" (ex: 2024-08)
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

        // 시작 날짜와 끝 날짜를 Date 객체로 파싱
        Date startDate = inputFormat.parse(searchfrDate);
        Date endDate = inputFormat.parse(searchtoDate);

        // Calendar 인스턴스 생성
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        // 시작 날짜에서 끝 날짜까지의 월을 초기화하여 0으로 셋팅
        while (!startCal.after(endCal)) {
            String yearMonth = yearMonthFormat.format(startCal.getTime());
            String year = String.valueOf(startCal.get(Calendar.YEAR));

            // 해당 연도에 해당하는 맵이 없으면 새로 생성
            countByYearMonth.putIfAbsent(year, new TreeMap<>());

            // 해당 연도의 특정 월을 0으로 초기화
            Map<String, Integer> countByMonth = countByYearMonth.get(year);
            countByMonth.putIfAbsent(yearMonth, 0);

            // 다음 달로 이동
            startCal.add(Calendar.MONTH, 1);
        }

        // 실제 데이터를 기반으로 카운트 증가
        for (Map<String, Object> item : items) {
            String hourStr = (String) item.get("checkdt");

            try {
                // checkdt를 Date 객체로 파싱
                Date hourDate = inputFormat.parse(hourStr);

                // 데이터가 검색 범위에 있는지 확인
                if (!hourDate.before(startDate) && !hourDate.after(endDate)) {
                    String yearMonth = yearMonthFormat.format(hourDate);
                    String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

                    // 해당 연도의 해당 월 카운트 증가
                    Map<String, Integer> countByMonth = countByYearMonth.get(year);
                    countByMonth.put(yearMonth, countByMonth.getOrDefault(yearMonth, 0) + 1);
                }

            } catch (ParseException e) {
                // 파싱 에러 처리
            }
        }

        return countByYearMonth;
    }


    private Map<String, Map<String, Integer>> getRmaTeGraphData_YTD(List<Map<String, Object>> items, String searchfrYear) throws ParseException {

        // TreeMap을 사용하여 연도를 기준으로 자동 정렬되도록 설정
        Map<String, Map<String, Integer>> countByYearMonth = new TreeMap<>();

        // items 리스트가 비었을 경우
        if (items.isEmpty()) {
            // searchfrYear에 해당하는 연도와 12개월을 0으로 초기화한 맵을 반환
            countByYearMonth.put(searchfrYear, initializeYearMonths(searchfrYear));
            return countByYearMonth;
        }

        // Input format for "yyyyMMdd" (ex: 20240827)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        // Output format for "yyyy-MM" (ex: 2024-08)
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

        for (Map<String, Object> item : items) {
            String hourStr = (String) item.get("checkdt");

            try {
                // Parse the hour string from "yyyyMMdd" format
                Date hourDate = inputFormat.parse(hourStr);
                // Format the date as "yyyy-MM" (with hyphen)
                String yearMonth = yearMonthFormat.format(hourDate);

                // Extract year from the date
                Calendar cal = Calendar.getInstance();
                cal.setTime(hourDate);
                String year = String.valueOf(cal.get(Calendar.YEAR));

                // 해당 연도의 카운트 맵을 가져오거나 없으면 새로 생성
                countByYearMonth.putIfAbsent(year, initializeYearMonths(year));

                // 해당 연도의 특정 달 카운트 증가
                Map<String, Integer> countByMonth = countByYearMonth.get(year);
                countByMonth.put(yearMonth, countByMonth.getOrDefault(yearMonth, 0) + 1);
            } catch (ParseException e) {
                // Handle parsing error if the date is unparseable
            }
        }

        // 누적 카운트를 계산하는 부분
        for (Map.Entry<String, Map<String, Integer>> yearEntry : countByYearMonth.entrySet()) {
            Map<String, Integer> countByMonth = yearEntry.getValue();
            int cumulativeCount = 0;

            for (String month : countByMonth.keySet()) {
                // 누적 카운트 계산 (이전 달까지의 값을 더함)
                cumulativeCount += countByMonth.get(month);
                countByMonth.put(month, cumulativeCount); // 누적된 값을 업데이트
            }
        }

        return countByYearMonth;
    }




    private Map<String, Map<String, Integer>> getRmaTeGraphData_YoY(List<Map<String, Object>> items, String searchfrYear) throws ParseException {

        // TreeMap을 사용하여 연도를 기준으로 자동 정렬되도록 설정
        Map<String, Map<String, Integer>> countByYearMonth = new TreeMap<>();

        // items 리스트가 비었을 경우
        if (items.isEmpty()) {
            // searchfrYear에 해당하는 연도와 12개월을 0으로 초기화한 맵을 반환
            countByYearMonth.put(searchfrYear, initializeYearMonths(searchfrYear));
            return countByYearMonth;
        }

        // Input format for "yyyyMMdd" (ex: 20240827)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        // Output format for "yyyy-MM" (ex: 2024-08)
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

        for (Map<String, Object> item : items) {
            String hourStr = (String) item.get("checkdt");

            try {
                // Parse the hour string from "yyyyMMdd" format
                Date hourDate = inputFormat.parse(hourStr);
                // Format the date as "yyyy-MM" (with hyphen)
                String yearMonth = yearMonthFormat.format(hourDate);

                // Extract year from the date
                Calendar cal = Calendar.getInstance();
                cal.setTime(hourDate);
                String year = String.valueOf(cal.get(Calendar.YEAR));

                // 해당 연도의 카운트 맵을 가져오거나 없으면 새로 생성
                countByYearMonth.putIfAbsent(year, initializeYearMonths(year));

                // 해당 연도의 특정 달 카운트 증가
                Map<String, Integer> countByMonth = countByYearMonth.get(year);
                countByMonth.put(yearMonth, countByMonth.getOrDefault(yearMonth, 0) + 1);
            } catch (ParseException e) {
                // Handle parsing error if the date is unparseable

            }
        }

        return countByYearMonth;
    }

    // 1월부터 12월까지 0으로 초기화하는 함수
    private Map<String, Integer> initializeYearMonths(String year) {
        Map<String, Integer> months = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            String yearMonth = String.format("%s-%02d", year, month);
            months.put(yearMonth, 0);
        }
        return months;
    }

    // 4분기를 0으로 초기화하는 함수 (연도 없이 분기만)
    private Map<String, Integer> initializeYearQuarters() {
        Map<String, Integer> quarters = new LinkedHashMap<>();
        quarters.put("Q1", 0);
        quarters.put("Q2", 0);
        quarters.put("Q3", 0);
        quarters.put("Q4", 0);
        return quarters;
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

                quarter = determineQuarter(month);

                /*if (month >= 1 && month <= 3) {
                    quarter = "Q1"; // 1st Quarter (Jan - Mar)
                } else if (month >= 4 && month <= 6) {
                    quarter = "Q2"; // 2nd Quarter (Apr - Jun)
                } else if (month >= 7 && month <= 9) {
                    quarter = "Q3"; // 3rd Quarter (Jul - Sep)
                } else {
                    quarter = "Q4"; // 4th Quarter (Oct - Dec)
                }*/

                // Create the key as "YYYY-QX"
                String yearQuarter = year + "-" + quarter;

                // Update the count for the specific quarter
                countByQuarter.put(yearQuarter, countByQuarter.getOrDefault(yearQuarter, 0) + 1);

            } catch (ParseException e) {
                // Handle parsing error if the date is unparseable

            }
        }
        return countByQuarter;
    }

    private Map<String, Map<String, Integer>> getRmaTeGraphData_QoQ(List<Map<String, Object>> items, String searchfrYear) throws ParseException {

        // TreeMap을 사용하여 연도를 기준으로 자동 정렬되도록 설정
        Map<String, Map<String, Integer>> countByYearQuarter = new TreeMap<>();

        // items 리스트가 비었을 경우
        if (items.isEmpty()) {
            // searchfrYear에 해당하는 연도와 4분기를 0으로 초기화한 맵을 반환
            countByYearQuarter.put(searchfrYear, initializeYearQuarters());
            return countByYearQuarter;
        }

        // Input format for "yyyyMMdd" (ex: 20240827)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");

        for (Map<String, Object> item : items) {
            String hourStr = (String) item.get("checkdt");

            try {
                // Parse the hour string from "yyyyMMdd" format
                Date hourDate = inputFormat.parse(hourStr);

                // Get the quarter for the date
                Calendar cal = Calendar.getInstance();
                cal.setTime(hourDate);
                String year = String.valueOf(cal.get(Calendar.YEAR));
                int month = cal.get(Calendar.MONTH) + 1; // 0-based, so add 1

                // Determine the quarter based on the month (Q1, Q2, Q3, Q4)
                String quarter = determineQuarter(month);

                // 해당 연도의 카운트 맵을 가져오거나 없으면 새로 생성
                countByYearQuarter.putIfAbsent(year, initializeYearQuarters());

                // 해당 연도의 특정 분기 카운트 증가
                Map<String, Integer> countByQuarter = countByYearQuarter.get(year);
                countByQuarter.put(quarter, countByQuarter.getOrDefault(quarter, 0) + 1);
            } catch (ParseException e) {
                // Handle parsing error if the date is unparseable

            }
        }

        return countByYearQuarter;
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

    // 분기를 계산하는 함수
    private String determineQuarter(int month) {
        if (month >= 1 && month <= 3) {
            return "Q1";
        } else if (month >= 4 && month <= 6) {
            return "Q2";
        } else if (month >= 7 && month <= 9) {
            return "Q3";
        } else {
            return "Q4";
        }
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
