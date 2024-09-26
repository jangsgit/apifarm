package mes.app.operate;

import com.fasterxml.jackson.databind.ObjectMapper;
import mes.app.actas_inspec.service.ElecSafeService;
import mes.app.common.CommonController;
import mes.app.common.service.CommonService;
import mes.app.operate.service.ControlService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.*;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP750Repository;
import mes.domain.repository.TB_RP760Repository;
import mes.domain.repository.TB_RP880Repository;
import mes.domain.repository.TB_RP885Repository;
import org.exolab.castor.types.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/operate/control")
public class ControlController {

    @Autowired
    ControlService controlService;

    @Autowired
    TB_RP880Repository TBRP880Repository;

    @Autowired
    TB_RP885Repository TBRP885Repository;

    @Autowired
    Settings settings;

    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate,
                              @RequestParam(value = "spworkcd") String spworkcd,
                              @RequestParam(value = "spcompcd") String spcompcd,
                              @RequestParam(value = "spplancd") String spplancd) {

        List<Map<String, Object>> items = new ArrayList<>();
        if (startDate == null) {
            startDate = "";
        }

        if (endDate == null) {
            endDate = "";
        }

        String c_startDate = startDate.replaceAll("-", "");
        String c_endDate = endDate.replaceAll("-", "");

        items = this.controlService.getList(c_startDate, c_endDate, spworkcd, spcompcd, spplancd);

        // 각 항목의 endresult 값을 변환
        for (Map<String, Object> item : items) {

            // 날짜 형식 변환 (registdt, checkdt)
            if (item.containsKey("contdt")) {
                String contdt = (String) item.get("contdt");
                if (contdt != null && contdt.length() == 8) {
                    String formattedDate = contdt.substring(0, 4) + "-" + contdt.substring(4, 6) + "-" + contdt.substring(6, 8);
                    item.put("contdt", formattedDate);
                }
            }

            if (item.containsKey("checkdt")) {
                String checkdt = (String) item.get("checkdt");
                if (checkdt != null && checkdt.length() == 8) {
                    String formattedDate = checkdt.substring(0, 4) + "-" + checkdt.substring(4, 6) + "-" + checkdt.substring(6, 8);
                    item.put("checkdt", formattedDate);
                }
            }
        }

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult saveControl(@RequestParam Map<String, String> params,
                                  Authentication auth) throws IOException, ParseException {

        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        String nspworkcd = params.get("spworkcd");
        String nspcompcd = params.get("spcompcd");
        String nspplancd = params.get("spplancd");
        String ncheckdt = params.get("checkdt").replaceAll("-", "");
        String ncontdt = params.get("contdt").replaceAll("-", "");

        TB_RP880_PK pk = new TB_RP880_PK();
        pk.setSpworkcd(nspworkcd);
        pk.setSpcompcd(nspcompcd);
        pk.setSpplancd(nspplancd);
        pk.setCheckdt(ncheckdt);
        pk.setContdt(ncontdt);

        // 날짜와 시간을 결합하여 LocalDateTime으로 변환
        LocalDate date = LocalDate.parse(params.get("contdt"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime startTime = LocalTime.parse(params.get("contstime"), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(params.get("contetime"), DateTimeFormatter.ofPattern("HH:mm"));

        // LocalDate와 LocalTime을 결합해 LocalDateTime 생성
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        // LocalDateTime을 Timestamp로 변환
        Timestamp startTimestamp = Timestamp.valueOf(startDateTime);
        Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

        TB_RP880 TBRP880 = new TB_RP880();
        TBRP880.setId(pk);
        TBRP880.setContstime(startTimestamp);
        TBRP880.setContetime(endTimestamp);
        TBRP880.setContdrive(params.get("contdrive"));
        TBRP880.setContusr(params.get("contusr"));
        TBRP880.setContarea(params.get("contarea"));
        TBRP880.setIndatem(now);
        TBRP880.setInusernm(user.getFirst_name());
        TBRP880.setInuserid(user.getUsername());


        AjaxResult result = new AjaxResult();

        boolean success = controlService.save(TBRP880);

        boolean success2 = false;
        try {
            if (params.get("contnum") != null && !params.get("contnum").isEmpty()) {
                // String 형식으로 받은 날짜와 시간 (예: "2024-09-24 18:24:00")
                String conttimeStr = params.get("conttime");

                // ISO 8601 형식으로 파싱 (yyyy-MM-dd'T'HH:mm)
                LocalDateTime localDateTime = LocalDateTime.parse(conttimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                // LocalDateTime을 Timestamp로 변환
                Timestamp contTime = Timestamp.valueOf(localDateTime);

                String ncontseqdt = conttimeStr.substring(0, 10).replace("-", "");

                Optional<String> maxContseqValue = TBRP885Repository.findMaxContseq(ncheckdt, ncontdt);
                String newContSeq = maxContseqValue.map(s -> String.valueOf(Integer.parseInt(s) + 1)).orElse("1");

                TB_RP885_PK pk2 = new TB_RP885_PK();
                pk2.setCheckdt(ncheckdt);
                pk2.setContdt(ncontdt);
                pk2.setContseq(newContSeq);

                // TB_RP885 객체 생성 및 값 설정
                TB_RP885 TBRP885 = new TB_RP885();
                TBRP885.setId(pk2);
                TBRP885.setContseqdt(ncontseqdt);
                TBRP885.setContnum(new BigDecimal(params.get("contnum")));
                TBRP885.setConttime(contTime);
                TBRP885.setContsequsr(params.get("contsequsr"));
                TBRP885.setIndatem(now);
                TBRP885.setInusernm(user.getFirst_name());
                TBRP885.setInuserid(user.getUsername());

                // 성공 여부 저장
                success2 = controlService.saveDetail(TBRP885);
            }
        } catch (Exception e) {
            result.success = false;
            result.message = "상세조치 저장 중 오류가 발생하였습니다: " + e.getMessage();
            return result;
        }

        if (success || success2) {

            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }
        return result;

    }

    @PostMapping("/save_detail")
    public AjaxResult saveControlDetail(@RequestParam Map<String, String> params,
                                        Authentication auth) throws IOException, ParseException {

        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AjaxResult result = new AjaxResult();

        // String 형식으로 받은 날짜와 시간 (예: "2024-09-24 18:24:00")
        String conttimeStr = params.get("conttime");

        // ISO 8601 형식으로 파싱 (yyyy-MM-dd'T'HH:mm)
        LocalDateTime localDateTime = LocalDateTime.parse(conttimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // LocalDateTime을 Timestamp로 변환
        Timestamp contTime = Timestamp.valueOf(localDateTime);

        String ncontseqdt = conttimeStr.substring(0, 10).replace("-", "");

        TB_RP885_PK pk2 = new TB_RP885_PK();

        pk2.setCheckdt(params.get("checkdt"));
        pk2.setContdt(params.get("contdt"));
        pk2.setContseq(params.get("contseq"));

        // TB_RP885 객체 생성 및 값 설정
        TB_RP885 TBRP885 = new TB_RP885();
        TBRP885.setId(pk2);
        TBRP885.setContseqdt(ncontseqdt);
        TBRP885.setContnum(new BigDecimal(params.get("contnum")));
        TBRP885.setConttime(contTime);
        TBRP885.setContsequsr(params.get("contsequsr"));
        TBRP885.setIndatem(now);
        TBRP885.setInusernm(user.getFirst_name());
        TBRP885.setInuserid(user.getUsername());

        // 성공 여부 저장
        boolean success = controlService.saveDetail(TBRP885);

        if (success) {

            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }
        return result;

    }

    @PostMapping("/modfind")
    public AjaxResult getById(@RequestBody TB_RP880_PK pk) throws IOException {
        AjaxResult result = new AjaxResult();

        Map<String, Object> item = controlService.findById(pk);

        // contstime과 contetime을 Map에서 가져온다
        Timestamp contstime = (Timestamp) item.get("contstime");
        Timestamp contetime = (Timestamp) item.get("contetime");

        // Timestamp를 현지 시간대로 변환하고 문자열로 변환
        if (contstime != null) {
            ZonedDateTime zonedContstime = contstime.toInstant().atZone(ZoneId.of("Asia/Seoul"));
            String timeForInputContstime = zonedContstime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            item.put("contstime", timeForInputContstime); // 변환한 시간을 다시 Map에 저장
        }

        if (contetime != null) {
            ZonedDateTime zonedContetime = contetime.toInstant().atZone(ZoneId.of("Asia/Seoul"));
            String timeForInputContetime = zonedContetime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            item.put("contetime", timeForInputContetime); // 변환한 시간을 다시 Map에 저장
        }

        result.data = item;
        return result;
    }

    @DeleteMapping("/delete")
    public AjaxResult deleteControl(@RequestBody TB_RP880_PK pk) {
        AjaxResult result = new AjaxResult();

        boolean success = controlService.delete(pk);

        if (success) {
            result.success = true;
            result.message = "삭제하였습니다.";
        } else {
            result.success = false;
            result.message = "삭제에 실패하였습니다.";
        }

        return result;
    }

    @DeleteMapping("/delete_detail")
    public AjaxResult deleteControlDetail(@RequestBody TB_RP885_PK pk) {
        AjaxResult result = new AjaxResult();

        try {
            Optional<TB_RP885> tbRp885 = TBRP885Repository.findById(pk);

            if (tbRp885.isPresent()) {
                TBRP885Repository.delete(tbRp885.get());
                result.success = true;
                result.message = "삭제하였습니다.";
            } else {
                result.success = false;
                result.message = "해당 항목을 찾을 수 없습니다.";
            }
        } catch (Exception e) {
            result.success = false;
            result.message = "삭제 중 오류가 발생했습니다: " + e.getMessage();
        }

        return result;
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(@RequestParam String query, @RequestParam String field) {
        List<String> suggestions = controlService.getSuggestions(query, field);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/newform")
    public AjaxResult getByIdforNew(@RequestParam(value = "spworkcd") String spworkcd,
                                    @RequestParam(value = "spcompcd") String spcompcd,
                                    @RequestParam(value = "spplancd") String spplancd) throws IOException {
        AjaxResult result = new AjaxResult();

        Map<String, Object> item = this.controlService.getFirst(spworkcd, spcompcd, spplancd);
        result.data = item;
        return result;
    }

}
