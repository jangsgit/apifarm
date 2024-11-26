package mes.app.cost.service;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import mes.domain.entity.actasEntity.TB_RP410;
import mes.domain.repository.actasRepository.TB_RP410Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mes.config.Settings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PdfProcessingService {

    @Autowired
    TB_RP410Repository  tbRp410Repository;


    private final Settings settings;

    public PdfProcessingService(Settings settings) {
        this.settings = settings;
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


//    pdf를 업로드 할때 jpg로 변환해서 pdf의 페이지 갯수만큼 읽어서 jpg로 변환시켜서 page_i 로 .jpg 파일을 만드는 코드
    // 동일한 파일이면 기존 파일에 대해서 덮어쓰기 되는거처럼 보임 ( 이부분 폴더에서 파일 삭제하고 다시 확인해봐야함 )
    public List<String> processPdf(String pdfFilePath) throws Exception {
        List<String> extractedTexts = new ArrayList<>();
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            int numberOfPages = document.getNumberOfPages();
//            logger.info("Total number of pages in PDF: " + numberOfPages);

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            String fileUploadPath = settings.getProperty("file_upload_path") + "도시가스청구서/";

            // 폴더가 없으면 생성
            Path directoryPath = Paths.get(fileUploadPath);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
//                logger.info("Directory created at: " + fileUploadPath);
            }


            for (int page = 0; page < numberOfPages; ++page) {
                try {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300);
                    String imagePath = fileUploadPath + "page_" + page + ".jpg";
//                    logger.info("Saving page " + page + " as image to: " + imagePath);

                    ImageIO.write(bim, "jpg", new File(imagePath));

                    String text = callClovaOcr(imagePath);
                    extractedTexts.add(text);
                } catch (Exception e) {
                    logger.error("Error processing page " + page, e);
                }
            }
        } catch (Exception e) {
            logger.error("Error loading PDF document", e);
            throw e;
        }
        return extractedTexts;
    }


    private static final Logger logger = LoggerFactory.getLogger(PdfProcessingService.class);

    private static final String OCR_API_URL ="https://4zma3djatn.apigw.ntruss.com/custom/v1/33910/7f65913827fdb9695f43e44196bee993e9d010e308ac41e6a79bf40412c947be/general";
    private static final String API_KEY_ID ="6hk6orv9tg"; // API Gateway에서 생성된 API 키의 ID
    private static final String SECRET_KEY ="S2xKb2JUUGJ2Z1hBbVlkVFdwZG9IVEtweFJrVU51VFA=";

    // Clova OCR API 호출을 위한 메소드
    private String callClovaOcr(String imagePath) throws IOException {

        File imageFile = new File(imagePath); // 이미지 파일 객체 생성
        String boundary = "----" + UUID.randomUUID().toString().replaceAll("-", ""); // 멀티파트 폼 데이터 경계 문자열 생성
        HttpURLConnection connection = (HttpURLConnection) new URL(OCR_API_URL).openConnection();
        connection.setUseCaches(false);  // 캐시 사용 안함
        connection.setDoInput(true); // 입력 스트림 사용
        connection.setDoOutput(true); // 출력 스트림 사용
        connection.setRequestMethod("POST"); // POST 요청 설정
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary); // 요청 헤더 설정
        connection.setRequestProperty("X-OCR-SECRET", SECRET_KEY); // 인증 헤더 설정
        connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", API_KEY_ID); // 인증 헤더 설정



//        // HTTP 요청 정보 로그 출력
//        logger.info("Request URL: " + OCR_API_URL);
//        logger.info("Request Method: POST");
//        logger.info("Content-Type: multipart/form-data; boundary=" + boundary);
//        logger.info("X-OCR-SECRET: " + SECRET_KEY);
//        logger.info("X-NCP-APIGW-API-KEY-ID: " + API_KEY_ID);


        // JSON 데이터 준비
        StringBuilder jsonPart = new StringBuilder();
        jsonPart.append("--").append(boundary).append("\r\n"); // 경계 문자열 추가
        jsonPart.append("Content-Disposition: form-data; name=\"message\"\r\n\r\n");
        jsonPart.append("{\n");
        jsonPart.append("  \"version\": \"V2\",\n");
        jsonPart.append("  \"requestId\": \"" + UUID.randomUUID().toString() + "\",\n"); // 요청 ID 생성
        jsonPart.append("  \"timestamp\": " + System.currentTimeMillis() + ",\n"); // 타임스탬프 설정
        jsonPart.append("  \"lang\": \"ko\",\n");  // 언어 설정
        jsonPart.append("  \"images\": [{\n");
        jsonPart.append("    \"format\": \"jpg\",\n"); // 이미지 포맷 설정
        jsonPart.append("    \"name\": \"sample\"\n"); // 이미지 이름 설정
        jsonPart.append("  }],\n");
        jsonPart.append("  \"enableTableDetection\": true\n"); // 테이블 감지 활성화
        jsonPart.append("}\r\n");

//        // JSON 부분 로그 출력
//        logger.info("Request JSON part: \n" + jsonPart.toString());

        // JSON 데이터를 출력 스트림에 쓰기
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(jsonPart.toString().getBytes("UTF-8"));


        // 이미지 파일 부분 준비
        StringBuilder filePart = new StringBuilder();
        filePart.append("--").append(boundary).append("\r\n");
        filePart.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + imageFile.getName() + "\"\r\n");
        filePart.append("Content-Type: application/octet-stream\r\n\r\n");


        // 파일 부분 로그 출력
//        logger.info("Request file part: \n" + filePart.toString());
        outputStream.write(filePart.toString().getBytes("UTF-8"));

        // 이미지 파일을 출력 스트림에 쓰기
        try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        // 멀티파트 데이터 끝을 표시
        outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();

        // 응답 처리
        int responseCode = connection.getResponseCode();
//        logger.info("Response Code: " + responseCode);

        BufferedReader reader;
        if (responseCode == 200) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String responseContent = response.toString();
//        logger.info("Raw response: \n" + responseContent);

        // 응답 데이터가 JSON 형식인지 확인하고, 파싱 시도
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonResponse = objectMapper.readTree(responseContent);
            return parseOcrResponse(jsonResponse.toString());
        } catch (JsonParseException e) {
            logger.error("JSON parsing error: " + e.getMessage());
            // JSON이 아닌 경우의 처리를 여기에 추가
            return "OCR response is not in JSON format.";
        }
    }

    // OCR 응답을 파싱하는 메소드
    public String parseOcrResponse(String response) {
        try {
            // JSON 응답을 파싱
            JsonNode rootNode = objectMapper.readTree(response);

            // 응답의 전체 구조를 로그로 기록
//            logger.info("Parsed JSON structure: " + rootNode.toPrettyString());

            StringBuilder extractedText = new StringBuilder();

            // "images" 노드를 확인
            JsonNode imagesNode = rootNode.path("images");
            if (imagesNode.isMissingNode() || !imagesNode.isArray()) {
                logger.error("Expected 'images' node missing or not an array.");
                return "Error parsing OCR response: 'images' node missing or not an array.";
            }

            for (JsonNode imageNode : imagesNode) {
                // "fields" 노드를 확인
                JsonNode fieldsNode = imageNode.path("fields");
                if (fieldsNode.isMissingNode() || !fieldsNode.isArray()) {
                    logger.error("Expected 'fields' node missing or not an array.");
                    return "Error parsing OCR response: 'fields' node missing or not an array.";
                }

                for (JsonNode fieldNode : fieldsNode) {
                    // "inferText" 노드를 확인
                    String text = fieldNode.path("inferText").asText();
                    extractedText.append(text).append("\n");
                }
            }
            return extractedText.toString().trim(); // 결과 텍스트 반환
        } catch (JsonProcessingException e) {
            // JSON 처리 오류
            logger.error("JSON processing error: ", e);
            // JSON이 아닌 경우 텍스트 데이터를 반환하는 로직으로 수정
            return response;  // JSON이 아닌 경우, 직접 텍스트를 반환합니다.
        } catch (Exception e) {
            // 기타 오류
            logger.error("OCR 응답 파싱 오류", e);
            return "Error parsing OCR response.";
        }
    }
}
