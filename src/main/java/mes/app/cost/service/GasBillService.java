package mes.app.cost.service;

import mes.domain.services.SqlRunner;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GasBillService {

    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getMonthlyUsageSummary(String year) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("year", year);

        String sql = """
                SELECT
                    SUBSTRING(STANDYM, 5, 2) AS month,
                    SUM(SMUSEQTY) AS smuseqty,
                    SUM(SMUSEHQTY) AS smusehqty,
                    SUM(ASKAMT) AS askamt
                FROM
                    TB_RP410
                WHERE
                    SUBSTRING(STANDYM, 1, 4) = :year
                GROUP BY
                    SUBSTRING(STANDYM, 5, 2)
                ORDER BY
                    month;
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, params);
        return items;
    }


    public List<String> getYear() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = """
                SELECT DISTINCT SUBSTRING(STANDYM, 1, 4) AS year
                FROM TB_RP410
                ORDER BY year DESC;
                """;

        List<Map<String, Object>> rows = this.sqlRunner.getRows(sql, params);
        List<String> years = rows.stream()
                .map(row -> (String) row.get("year"))
                .collect(Collectors.toList());
        return years;
    }


    public List<Map<String, Object>> getmainData(String startDate, String endDate, String searchType) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        // SQL 파라미터 설정
        params.addValue("startDate", startDate);
        params.addValue("endDate", endDate);
        params.addValue("searchType", searchType);

        // SQL 쿼리 작성 (SQL의 기준은 STANDYM을 사용하여 날짜 필터링)
        String sql = """
            SELECT SPWORKCD, SPWORKNM, SPCOMPCD, SPCOMPNM, SPPLANCD, SPPLANNM, 
                   STANDYM, GASUSEAMT, METERMGAMT, IMTARRAMT, SAFEMGAMT, SUPPAMT, TAXAMT, 
                   TRUNAMT, ASKAMT, USEUAMT, SMUSEQTY, SMUSEHQTY, LMUSEQTY, LMUSEHQTY, 
                   LYUSEQTY, LYUSEHQTY, INUSERID, INUSERNM, INDATEM
            FROM TB_RP410
            WHERE STANDYM >= :startDate
              AND STANDYM <= :endDate
            """;

        // 검색 유형에 따른 추가 필터링 (필요한 경우)
        if ("monthly".equals(searchType)) {
            sql += " AND LENGTH(STANDYM) = 6";  // 월별 검색 (YYYYMM 형식)
        } else if ("yearly".equals(searchType)) {
            sql += " AND LENGTH(STANDYM) = 4";  // 연도별 검색 (YYYY 형식)
        }

        // SQL 실행 및 결과 조회
        List<Map<String, Object>> rows = this.sqlRunner.getRows(sql, params);

        // 조회된 결과 반환
        return rows;
    }

}

