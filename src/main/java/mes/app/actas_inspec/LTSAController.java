package mes.app.actas_inspec;


import mes.app.PDFReader;
import mes.domain.entity.actasEntity.TB_RP810;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@RestController
@RequestMapping("/api/ltsa")
public class LTSAController {


    @PostMapping("/pdfReader")
    public String uploadPDF(@RequestParam("file") MultipartFile file) throws IOException {
        // 파일을 임시 디렉터리에 저장
        File tempFile = File.createTempFile("uploaded-", ".pdf"); // 임시 파일 생성
        file.transferTo(tempFile);  // 파일을 임시 파일로 변환

        // PDF 파일에서 텍스트 추출
        PDFReader pdfReader = new PDFReader();
        String pdfText = pdfReader.extractTextFromPDF(tempFile, 1,3);

        String regStr_MogJeog = "목적\\s*:\\s*(.*?)(?=\\n|\\r)";
        String regStr_ProjectName = "프로젝트명\\s*:\\s*(.*?)(?=\\n|\\r)";
        String regStr_SeolBi = "설비현황\\s*:\\s*(.*?)(?=\\n|\\r)";
        String regStr_BogoseoGaeyo = "보고서 개요\\s*:\\s*(.*?)(?=\\n|\\r)";


        String test = extractText(pdfText, regStr_MogJeog);
        String test2 = extractText(pdfText, regStr_ProjectName);
        String test3 = extractText(pdfText, regStr_SeolBi);
        String test4 = extractText(pdfText, regStr_BogoseoGaeyo);

        String extractedText = extractPerformanceGuarantee(pdfText);

        // 문자열을 줄바꿈 기준으로 분리하여 List<String>에 저장
        List<String> lines = new ArrayList<>(Arrays.asList(extractedText.split("\\r?\\n")));



        // 추출된 텍스트 출력
        System.out.println(pdfText);
        System.out.println("===================================================================");
        System.out.println(test);
        System.out.println(test2);
        System.out.println(test3);
        System.out.println(test4);
        System.out.println("===================================================================");
        System.out.println(extractedText);
// 결과 출력

        // 파일이 사용된 후 삭제
        tempFile.deleteOnExit(); // 임시 파일 자동 삭제

        return "ok";
    }

    public String extractText(String text, String regStr){
        Pattern pattern = Pattern.compile(regStr);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : "No Found";

    }


    public static String extractPerformanceGuarantee(String text) {
        // '성능보증' 키워드를 포함하는 문장을 찾는 정규식
        Pattern startPattern = Pattern.compile("계약자 성능보증 사항\\s*:\\s*(.*?)(?=\\n|\\r)", Pattern.MULTILINE);
        // 다음 숫자로 시작하는 항목을 찾는 정규식 (다음 항목을 추출하기 위한 패턴)
        Pattern endPattern = Pattern.compile("\\n\\d+\\.\\s", Pattern.MULTILINE);

        // 시작 구간 추출
        Matcher startMatcher = startPattern.matcher(text);
        if (startMatcher.find()) {
            int startIndex = startMatcher.start();

            // 시작 구간 이후에 나오는 다음 번호로 시작하는 항목 추출
            Matcher endMatcher = endPattern.matcher(text);
            if (endMatcher.find(startIndex)) {
                int endIndex = endMatcher.start();

                // 시작과 끝 사이의 텍스트를 추출
                return text.substring(startIndex, endIndex).trim();
            } else {
                // 다음 항목이 없으면 텍스트 끝까지 추출
                return text.substring(startIndex).trim();
            }
        }
        return "성능보증 구간을 찾을 수 없습니다.";
    }

    public static Map<String, String> parseTableData(String text) {
        Map<String, String> tableData = new HashMap<>();

        // 데이터 추출을 위한 정규식 패턴 정의
        Pattern pattern = Pattern.compile("(\\d+% 이상)\\s+(\\d+% 이상)\\s+(.*?기준)\\n(\\d+% 이상)\\s+\\((.*?)kcal/kWh\\)\\n(\\d+% 이상)\\s+\\((.*?)kcal/kWh\\)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            tableData.put("연간 누적 이용율(%) 보증조건", matcher.group(1)); // "95% 이상"
            tableData.put("연간 누적 이용율(%) 워런티조건", matcher.group(2)); // "90% 이상"
            tableData.put("연간 누적 효율(kcal/kWh) 보증조건", matcher.group(3) + " (" + matcher.group(4) + ")"); // "56% 이상 (1,536.5kcal/kWh)"
            tableData.put("연간 누적 효율(kcal/kWh) 워런티조건", matcher.group(5) + " (" + matcher.group(6) + ")"); // "54% 이상 (1,593.4kcal/kWh)"
        }

        return tableData;
    }

}

