package mes.app.system;


import mes.app.account.service.TB_RP940_Service;
import mes.app.system.service.AuthListService;
import mes.domain.DTO.TB_RP940Dto;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP940Repository;
import mes.domain.repository.TB_RP945Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/system/auth_list")
public class AuthListController {

    @Autowired
    AuthListService authListService;

    @Autowired
    TB_RP940Repository tb_rp940Repository;

    @Autowired
    TB_RP945Repository tb_rp945Repository;

    @Autowired
    TB_RP940_Service tbRp940Service;

    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "searchusr", required = false) String searchusr,
                              @RequestParam(value = "searchfrdate", required = false) String searchfrdate,
                              @RequestParam(value = "searchtodate", required = false) String searchtodate,
                              @RequestParam(value = "searchflag", required = false) String flag,
                              @RequestParam(value = "searchuserid", required = false) String searchuserid) throws ParseException {
        List<Map<String, Object>> items = new ArrayList<>();

        searchusr = Optional.ofNullable(searchusr).orElse("");
        searchfrdate = Optional.ofNullable(searchfrdate).orElse("20000101");
        searchtodate = Optional.ofNullable(searchtodate).orElse("29991231");
        searchuserid = Optional.ofNullable(searchuserid).orElse("");


        if(searchfrdate.isEmpty()){
            searchfrdate = "20000101";
        }
        if(searchtodate.isEmpty()){
            searchtodate = "29991231";
        }

        Timestamp startTime = convertToTimestamp(searchfrdate.replaceAll("-","") + "000000");
        Timestamp endTime = convertToTimestamp(searchtodate.replaceAll("-","") + "235959");



        items = authListService.getAuthList(searchusr, startTime, endTime, flag, searchuserid);

        // Timestamp 값을 String으로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX");
        for (Map<String, Object> item : items) {
            Object appdatem = item.get("appdatem");
            if (appdatem instanceof Timestamp) {
                String formattedDate = dateFormat.format((Timestamp) appdatem);
                item.put("appdatem", formattedDate.substring(0,19));
            }

            Object askdatem = item.get("askdatem");
            if(askdatem instanceof Timestamp){
                String formattedDate = dateFormat.format((Timestamp) askdatem);
                item.put("askdatem", formattedDate.substring(0,19));

            }
        }



        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    @Transactional
    public AjaxResult saveFilter(
            @RequestParam(value = "userid") String userid,
            @RequestParam(value = "appflag") String appflag
    ){
        AjaxResult result = new AjaxResult();

        // 현재 시간을 Asia/Seoul 시간대로 가져오기
        ZonedDateTime seoulDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        OffsetDateTime offsetDateTime = seoulDateTime.toOffsetDateTime();

        try{
           if(appflag.equals("Y")){
               tb_rp940Repository.updateApprflagToYByUserid(userid, offsetDateTime);

           }else{
               tb_rp940Repository.updateApprflagToYByUserid(userid, appflag);
           }

            result.success = true;
            result.message = "승인이 완료되었습니다.";
            return result;
        }catch(Exception e){
            System.out.println(e);

            result.success = false;
            result.message = "에러가발생하였습니다.";
            return result;
        }


    }

    private static Timestamp convertToTimestamp(String dateTimeString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        Date parseDate = inputFormat.parse(dateTimeString);

        return new Timestamp(parseDate.getTime());

    }


    @PostMapping("/delete")
    @Transactional
    public AjaxResult delete(@RequestParam(value = "userid") String userid){
        AjaxResult result = new AjaxResult();

        String cleanJson = userid.replaceAll("[\\[\\]\"]", "");
        String[] tokens = cleanJson.split(",");

        List<String> paramList = List.of(tokens);

        for(String param : paramList){
            tb_rp940Repository.deleteByUserid(param);
            tb_rp945Repository.deleteByUserid(param);

        }

        result.success = true;
        result.message = "성공";
        return result;
    }

    @PostMapping("/approve")
    @Transactional
    public AjaxResult approve(@RequestParam(value = "userid") String userid)
    {

        AjaxResult result = new AjaxResult();

        String cleanJson = userid.replaceAll("[\\[\\]\"]", "");
        String[] tokens = cleanJson.split(",");

        List<String> paramList = List.of(tokens);

        for(String param : paramList){
            System.out.println(param);

            //tb_rp940Repository.updateApprflagToYByUserid(param);

        }


        result.success = true;
        result.message = "성공";
        return result;
    }
}
