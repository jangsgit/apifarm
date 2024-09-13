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

}

