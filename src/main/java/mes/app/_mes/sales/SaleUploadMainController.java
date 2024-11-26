package mes.app._mes.sales;


import mes.config.Settings;
import mes.domain.model.AjaxResult;
import mes.domain.services.CommonUtil;
import mes.domain.services.SqlRunner;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/sale/upload/sale_upload_main")
public class SaleUploadMainController {

    @Autowired
    Settings settings;

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    private mes.app.sales.service.SujuUploadService sujuUploadService;
    @Autowired
    private View error;

    @GetMapping("/read")
    public AjaxResult getSujuUploadList(@RequestParam(value = "start", required = false) String start_date, @RequestParam(value = "end", required = false) String end_date,
    @RequestParam(value = "flag", required = false) String flag){

        List<Map<String, Object>> items = new ArrayList<>();

        if(flag.equals("upload")){
             items = this.sujuUploadService.getSujuUploadList();
        } else if (flag.equals("list")) {
             items = this.sujuUploadService.getSujuUploadList_sale_list(start_date, end_date);

        }

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/upload_save")
    public AjaxResult saveData(@RequestParam(value = "upload_file") MultipartFile upload_file,
                               MultipartHttpServletRequest multipartRequest, Authentication auth) throws FileNotFoundException, IOException {

        //User user = (User)auth.getPrincipal();

        int number_col = 0; //번호
        int date_col = 1; //일자
        int Baljungi_code_col = 2; //발전기코드
        int Baljungi_name_col = 3; //발전기명
        int charge_col = 4; //충전or방전
        List<Integer> intList = Arrays.asList(5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28); // 01~ 24시
        int sum_col = 29; //합계

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = dtf.format(now);
        String upload_filename = settings.getProperty("file_temp_upload_path") + formattedDate + "_" + upload_file.getOriginalFilename();

        if(new File(upload_filename).exists()){
            new File(upload_filename).delete();
        }
        try (FileOutputStream destication = new FileOutputStream(upload_filename)) {
            destication.write(upload_file.getBytes());
        }

        List<List<String>> suju_file = this.sujuUploadService.excel_read(upload_filename);
        Map<String, Object> error_items = new HashMap<String, Object>();



        for(int i=0; i < suju_file.size(); i++){

            List<String> timelist = new ArrayList<>();
            List<String> row = suju_file.get(i);

            double value2 = Double.parseDouble(row.get(number_col));
            int integerValue2 = (int) value2;
            String stringvalue2 = String.valueOf(integerValue2);

            String number = String.valueOf(stringvalue2);
            String date = row.get(date_col);
            String Baljungi_code = row.get(Baljungi_code_col);
            String Baljungi_name = row.get(Baljungi_name_col);
            String charge = row.get(charge_col);
            for(int j=0; j < intList.size(); j++){
                double value = Double.parseDouble(row.get(intList.get(j)));
                int integerValue = (int) value;
                String stringvalue = String.valueOf(integerValue);
                timelist.add(stringvalue);
            }
            String sum = row.get(sum_col);

            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            paramMap.addValue("number", number);
            paramMap.addValue("date", date);
            paramMap.addValue("Baljungi_code", Baljungi_code);
            paramMap.addValue("Baljungi_name", Baljungi_name);
            paramMap.addValue("charge", charge);
            paramMap.addValue("time01", timelist.get(0));
            paramMap.addValue("time02", timelist.get(1));
            paramMap.addValue("time03", timelist.get(2));
            paramMap.addValue("time04", timelist.get(3));
            paramMap.addValue("time05", timelist.get(4));
            paramMap.addValue("time06", timelist.get(5));
            paramMap.addValue("time07", timelist.get(6));
            paramMap.addValue("time08", timelist.get(7));
            paramMap.addValue("time09", timelist.get(8));
            paramMap.addValue("time10", timelist.get(9));
            paramMap.addValue("time11", timelist.get(10));
            paramMap.addValue("time12", timelist.get(11));
            paramMap.addValue("time13", timelist.get(12));
            paramMap.addValue("time14", timelist.get(13));
            paramMap.addValue("time15", timelist.get(14));
            paramMap.addValue("time16", timelist.get(15));
            paramMap.addValue("time17", timelist.get(16));
            paramMap.addValue("time18", timelist.get(17));
            paramMap.addValue("time19", timelist.get(18));
            paramMap.addValue("time20", timelist.get(19));
            paramMap.addValue("time21", timelist.get(20));
            paramMap.addValue("time22", timelist.get(21));
            paramMap.addValue("time23", timelist.get(22));
            paramMap.addValue("time24", timelist.get(23));
            paramMap.addValue("sum", sum);

            String sql =
                    "INSERT INTO public.suju_bulk2(" +
                        "\"number\", \"date\", \"Baljungi_code\", \"Baljungi_name\", \"charge\", \"time01\", \"time02\", \"time03\", \"time04\", \"time05\", \"time06\", \"time07\", " +
                            "\"time08\", \"time09\", \"time10\", \"time11\", \"time12\", \"time13\", \"time14\", \"time15\", \"time16\", " +
                            "\"time17\", \"time18\", \"time19\", \"time20\", \"time21\", \"time22\", \"time23\", \"time24\", \"sum\" "+
                            ") VALUES (" +
                            ":number, :date, :Baljungi_code, :Baljungi_name, :charge, :time01, :time02, :time03, :time04, :time05, :time06, :time07, :time08, :time09, :time10, :time11, " +
                            ":time12, :time13, :time14, :time15, :time16, :time17, :time18 , :time19, :time20, :time21, :time22, :time23 , :time24, :sum" +
                            ")";

                            String log_data =
                    "index : " + i +
                    ", number : " + number +
                    ", date : " + date +
                    ", Baljungi_code : " + Baljungi_code +
                    ", Baljungi_name : " + Baljungi_name +
                    ", charge : " + charge +
                    ", time01 : " + timelist.get(0)  +
                    ", time02 : " + timelist.get(1)  +
                    ", time03 : " + timelist.get(2)  +
                    ", time04 : " + timelist.get(3)  +
                    ", time05 : " + timelist.get(4)  +
                    ", time06 : " + timelist.get(5)  +
                    ", time07 : " + timelist.get(6)  +
                    ", time08 : " + timelist.get(7)  +
                    ", time09 : " + timelist.get(8)  +
                    ", time10 : " + timelist.get(9)  +
                    ", time11 : " + timelist.get(10) +
                    ", time12 : " + timelist.get(11) +
                    ", time13 : " + timelist.get(12) +
                    ", time14 : " + timelist.get(13) +
                    ", time15 : " + timelist.get(14) +
                    ", time16 : " + timelist.get(15) +
                    ", time17 : " + timelist.get(16) +
                    ", time18 : " + timelist.get(17) +
                    ", time19 : " + timelist.get(18) +
                    ", time20 : " + timelist.get(19) +
                    ", time21 : " + timelist.get(20) +
                    ", time22 : " + timelist.get(21) +
                    ", time23 : " + timelist.get(22) +
                    ", time24 : " + timelist.get(23) +
                    ", sum : " + sum;
            try {
                if(Baljungi_code != null && !Baljungi_code.isEmpty() && Baljungi_name != null && !Baljungi_name.isEmpty()){
                    this.sqlRunner.execute(sql, paramMap);

                }
            } catch (Exception e){
                error_items.put("log",log_data);
                error_items.put("ex",e.getMessage());
            }
        }

        AjaxResult result = new AjaxResult();
        result.success=true;

        if( error_items.size() > 0 )
        {
            result.success=false;
        }
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("error_items", error_items);

        result.data=item;
        return result;

    }

    @PostMapping("/delete")
    public AjaxResult deleteSujuBulkData(@RequestParam MultiValueMap<String, Object> Q, HttpServletRequest request) {

        AjaxResult result = new AjaxResult();

        String sql = "";
        List<Map<String, Object>> qItems = CommonUtil.loadJsonListMap(Q.getFirst("Q").toString());

        if(qItems.size() == 0){
            result.success = false;
            return result;
        }

        for(int i = 0; i < qItems.size(); i++) {
            String number = qItems.get(i).get("number").toString();

            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            paramMap.addValue("number", number);


                sql = """
					delete from suju_bulk2 where number = :number
				  """;
                this.sqlRunner.execute(sql, paramMap);
                result.success=true;

        }

        return result;
    }

    @GetMapping("/detail")
    public AjaxResult getUserGroup(@RequestParam("number") String number ){

        Map<String, Object> item = this.sujuUploadService.getUserGroup(number);

        AjaxResult result = new AjaxResult();
        result.data = item;
        return result;
    }



}
