package mes.app.system.service;

import mes.domain.entity.UserCode;
import mes.domain.repository.TB_RP980Repository;
import mes.domain.repository.UserCodeRepository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EtctelListService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    private TB_RP980Repository tp980Repository;



    @Autowired
    private UserCodeService userCodeService;

    public List<Map<String, Object>> getEtctelList(String emconper, String emconmno, String emconcomp) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("emconper", "%" + emconper + "%");
        params.addValue("emconmno", "%" + emconmno + "%");
        params.addValue("emconcomp", "%" + emconcomp + "%");

        String sql = """
        SELECT
            ROW_NUMBER() OVER (ORDER BY tb980.indatem DESC) AS rownum,
            COALESCE(uc."Value", tb980.spworkcd) AS spworkcd,
            COALESCE(uc2."Value", tb980.spcompcd) AS spcompcd,
            tb980.spcompcd AS spcompcd,
            tb980.emconcomp AS emconcomp,
            tb980.indatem AS indatem,
            tb980.emconper AS emconper,
            tb980.divinm AS divinm,
            tb980.taskwork AS taskwork,
            tb980.emcontel AS emcontel,
            tb980.emconmno AS emconmno,
            tb980.emconemail AS emconemail,
            tb980.emcontno AS emcontno
            FROM tb_rp980 tb980
            LEFT JOIN user_code uc ON tb980.spworkcd = uc."Code"
            LEFT JOIN user_code uc2 ON tb980.spcompcd = uc2."Code"
            WHERE
                tb980.emconper LIKE :emconper
                AND tb980.emconmno LIKE :emconmno
                AND tb980.emconcomp LIKE :emconcomp
            ORDER BY tb980.indatem DESC
        """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, params);
        return items;
    }

    // 새로운 emcontno 생성
    public String generateNewEmcontno() {
        Optional<Long> maxEmcontno = tp980Repository.findMaxEmcontno();
        Long newEmcontno = maxEmcontno.map(value -> value + 1).orElse(1L);


        return String.format("%03d", newEmcontno);
    }

    // 모든 지역과 해당 지역의 산단 목록을 반환
    public Map<String, List<Map<String, String>>> getRegionsWithDistricts() {
        // 서비스 메소드 호출하여 원본 데이터 가져오기
        Map<String, Map<String, Object>> rawData = userCodeService.getRegionsWithDistricts();

        // 결과를 위한 Map
        Map<String, List<Map<String, String>>> processedData = new HashMap<>();

        // rawData를 처리하여 processedData를 채운다
        for (Map.Entry<String, Map<String, Object>> entry : rawData.entrySet()) {
            String regionCode = entry.getKey();
            Map<String, Object> regionData = entry.getValue();

            // 지역 이름 추출
            String regionName = (String) regionData.get("regionName");

            // 지역에 해당하는 산단 목록을 추출
            List<Map<String, String>> districtMaps = (List<Map<String, String>>) regionData.get("districts");

            // 결과를 저장할 리스트
            List<Map<String, String>> districtData = new ArrayList<>();

            if (districtMaps != null) {
                for (Map<String, String> districtMap : districtMaps) {
                    // 지역 코드와 지역 이름을 포함한 산단 데이터 생성
                    Map<String, String> newDistrictMap = new HashMap<>();
                    newDistrictMap.put("districtName", districtMap.get("districtName"));
                    newDistrictMap.put("districtCode", districtMap.get("districtCode"));
                    newDistrictMap.put("regionName", regionName); // 지역 이름 추가
                    newDistrictMap.put("regionCode", regionCode);  // 지역 코드 추가

                    districtData.add(newDistrictMap);
                }
            }

            // 결과 데이터를 저장
            processedData.put(regionCode, districtData);
        }

        return processedData;
    }

}

