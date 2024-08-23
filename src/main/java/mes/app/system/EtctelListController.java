package mes.app.system;


import mes.app.system.service.EtctelListService;

import mes.app.system.service.UserCodeService;
import mes.domain.entity.TB_RP980;
import mes.domain.entity.User;
import mes.domain.entity.UserCode;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP980Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/system/tbRp980")
public class EtctelListController {

    @Autowired
    EtctelListService etctelListService;

    @Autowired
    TB_RP980Repository tp980Repository;

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    private UserCodeService userCodeService;

    @GetMapping("/read")
    public AjaxResult getList(
            @RequestParam(value = "emconper", required = false) String emconper,
            @RequestParam(value = "emconmno", required = false) String emconmno,
            @RequestParam(value = "emconcomp", required = false) String emconcomp) {

        if (emconper == null) {
            emconper = "";
        }
        if (emconmno == null) {
            emconmno = "";
        }
        if (emconcomp == null) {
            emconcomp = "";
        }

        List<Map<String, Object>> items = this.etctelListService.getEtctelList(emconper, emconmno, emconcomp);
        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult addEtctelList(
            @RequestParam(value = "emcontno", required = false) String emcontno,
            @RequestParam(value = "emconcomp", required = false) String emconcomp,
            @RequestParam(value = "emconper", required = false) String emconper,
            @RequestParam(value = "emcontel", required = false) String emcontel,
            @RequestParam(value = "indatem", required = false) String indatemStr,
            @RequestParam(value = "inuserid", required = false) String inuserid,
            @RequestParam(value = "inusernm", required = false) String inusernm,
            @RequestParam(value = "emconemail", required = false) String emconemail,
            @RequestParam(value = "spworkcd", required = false) String spworkcd,
            @RequestParam(value = "spcompcd", required = false) String spcompcd,
            @RequestParam(value = "taskwork", required = false) String taskwork,
            @RequestParam(value = "divinm", required = false) String divinm,
            @RequestParam(value = "emconmno", required = false) String emconmno,
            HttpServletRequest request,
            Authentication auth) {

        // 현재 사용자 정보 가져오기
        User user = (User) auth.getPrincipal();

        TB_RP980 tbRp980;

        if (emcontno == null || emcontno.isEmpty()) {
            tbRp980 = new TB_RP980();
            emcontno = etctelListService.generateNewEmcontno(); // 수동으로 ID 생성
            tbRp980.setEmcontno(emcontno);
        } else {
            tbRp980 = this.tp980Repository.findById(emcontno).orElse(new TB_RP980());
            tbRp980.setEmcontno(emcontno); // 기존 ID 사용
        }


            tbRp980.setEmconcomp(emconcomp);    // 협력사 명
            tbRp980.setEmconper(emconper);      // 담당자
            tbRp980.setEmcontel(emcontel);      // 사무실번호
            tbRp980.setEmconmno(emconmno);      // 모바일
            tbRp980.setEmconemail(emconemail);  // 이메일
            tbRp980.setTaskwork(taskwork);
            tbRp980.setDivinm(divinm);          // 소속부서
            // 날짜 문자열을 java.sql.Date로 변환
            if (indatemStr != null && !indatemStr.isEmpty()) {
                LocalDate localDate = LocalDate.parse(indatemStr); // 문자열을 LocalDate로 변환
                tbRp980.setIndatem(java.sql.Date.valueOf(localDate)); // LocalDate를 java.sql.Date로 변환
            }
            tbRp980.setInuserid(String.valueOf(user.getId()));  // 입력자 ID
            tbRp980.setInusernm(user.getUsername());    // 입력자 이름
            tbRp980.setSpworkcd(spworkcd);  // 관할 지역 코드
            tbRp980.setSpcompcd(spcompcd); // 발전 산단 코드


        AjaxResult result = new AjaxResult();

        try {

            tbRp980 = this.tp980Repository.save(tbRp980);

        } catch (Exception e) {
            e.printStackTrace();
            result.success = false;
            result.message = "데이터베이스 저장 중 오류가 발생했습니다.";
        }

            result.data = tbRp980;

            return result;
        }

    @GetMapping("/regions")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getRegionsWithDistricts() {
        // 서비스 메소드 호출하여 지역과 산단 데이터를 가져온다
        Map<String, Map<String, Object>> rawRegionsWithDistricts = userCodeService.getRegionsWithDistricts();

        // 변환된 결과를 저장할 Map
        Map<String, List<Map<String, String>>> convertedRegionsWithDistricts = new HashMap<>();

        // rawRegionsWithDistricts를 변환하여 convertedRegionsWithDistricts에 저장
        for (Map.Entry<String, Map<String, Object>> entry : rawRegionsWithDistricts.entrySet()) {
            String regionCode = entry.getKey();
            Map<String, Object> regionData = entry.getValue();

            // 지역 이름 추출
            String regionName = (String) regionData.get("regionName");

            // 지역 코드와 이름을 포함하는 리스트 생성
            List<Map<String, String>> districtList = new ArrayList<>();
            List<Map<String, String>> districts = (List<Map<String, String>>) regionData.get("districts");

            if (districts != null) {
                for (Map<String, String> district : districts) {
                    Map<String, String> districtData = new HashMap<>();
                    districtData.put("districtName", district.get("districtName"));
                    districtData.put("districtCode", district.get("districtCode"));
                    districtData.put("regionName", regionName); // 지역 이름 추가
                    districtData.put("regionCode", regionCode);  // 지역 코드 추가

                    districtList.add(districtData);
                }
            }

            // 변환된 데이터 저장
            convertedRegionsWithDistricts.put(regionCode, districtList);
        }

        return ResponseEntity.ok(convertedRegionsWithDistricts);
    }


    @PostMapping("/delete")
    @Transactional
    public AjaxResult deleteCode(@RequestParam("emcontno") String emcontno) {
        this.tp980Repository.deleteById(emcontno);
        AjaxResult result = new AjaxResult();

        return result;
    }

}

