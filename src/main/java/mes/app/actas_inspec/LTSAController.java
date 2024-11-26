package mes.app.actas_inspec;



import mes.app.PDFReader;
import mes.app.actas_inspec.service.PDFTableProcessor;
import mes.domain.DTO.TB_RP620Dto;
import mes.domain.DTO.TB_RP621Dto;
import mes.domain.DTO.TB_RP622Dto;
import mes.domain.entity.actasEntity.TB_RP620;
import mes.domain.entity.actasEntity.TB_RP621;
import mes.domain.entity.actasEntity.TB_RP622;
import mes.domain.entity.actasEntity.TB_RP810;
import mes.domain.repository.TB_RP620Repository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xlsx4j.sml.Row;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

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

    @Autowired
    TB_RP620Repository tbRp620Repository;

    private PDDocument pdDocument;
    private ObjectExtractor extractor;
    private List<String> pageTexts; // 페이지별 텍스트 저장
    private List<List<Table>> allTables; // 각 페이지의 테이블 저장


    @PostMapping("/pdfReader")
    public void uploadPDF(@RequestParam("file") MultipartFile file,
                          @RequestParam("spworkcd") String spworkcd,
                          @RequestParam("spcompcd") String spcompcd,
                          @RequestParam("spplancd") String spplancd,
                          @RequestParam("spworknm") String spworknm,
                          @RequestParam("spcompnm") String spcompnm,
                          @RequestParam("spplannm") String spplannm
    ) throws IOException {

        // 임시 파일 생성 및 저장
        File tempFile = saveFileToTemp((MultipartFile) file);

        PDFTableProcessor processor = new PDFTableProcessor();
        processor.loadPDF(tempFile); // 한 번만 PDF를 로드

        PDFReader pdfReader = new PDFReader();
        String pdfText = pdfReader.extractTextFromPDF(tempFile, 1, 3);
        String pdfText2 = pdfReader.extractTextFromPDF(tempFile, 1,1);


        Map<String, String> extractedValues = extractMultiplePatterns(pdfText);



        Map<String, List<List<String>>> results3 = processor.extractRowsAfterText();

        //results3.get("table1").get()

        String Reportout =  extractPerformanceGuarantee(pdfText, "보고서 개요");
        String purpose = extractPerformanceGuarantee(pdfText, "목적");
        String Sulbi = extractPerformanceGuarantee(pdfText, "설비현황");


        System.out.println("Report:   " + extractedValues.get("프로젝트명"));
        System.out.println("Report:   " + extractedValues.get("실적기간"));
        System.out.println("Report:   " + extractedValues.get("보고서"));
        System.out.println("Report23:   " + extractYearAndQuarter(pdfText2).get(0));
        System.out.println("Report32:   " + extractYearAndQuarter(pdfText2).get(1));


        List<String> StandqyValue = extractYearAndQuarter(pdfText2);

        String CurrentYear = StandqyValue.get(0);
        String CurrentDay = StandqyValue.get(1);
        List<String> quarterDay = QuarterToDay(CurrentDay);
        Map<String, String> dtoValue = new HashMap<>();
        dtoValue.put("spworkcd", spworkcd); dtoValue.put("spworknm", spworknm);
        dtoValue.put("spcompcd", spcompcd); dtoValue.put("spcompnm", spcompnm);
        dtoValue.put("spplancd", spplancd); dtoValue.put("spplannm", spplannm);
        dtoValue.put("CurrentYear", CurrentYear); dtoValue.put("CurrentDay", CurrentDay);

        /**LTSA 보고서 개요 정보 저장**/
        TB_RP620Dto RP620Dto = new TB_RP620Dto();

        RP620Dto.setSpworkcd(spworkcd); RP620Dto.setSpworknm(spworknm);
        RP620Dto.setSpcompcd(spcompcd); RP620Dto.setSpcompnm(spcompnm);
        RP620Dto.setSpplancd(spplancd); RP620Dto.setSpplannm(spplannm);
        RP620Dto.setStandqy(CurrentYear + CurrentDay);
        RP620Dto.setPurpose(purpose);
        RP620Dto.setProjectnm(extractedValues.get("프로젝트명"));
        RP620Dto.setEquipstat(Sulbi);
        RP620Dto.setReportout(Reportout);
        RP620Dto.setResultsdt(CurrentYear + quarterDay.get(0));
        RP620Dto.setResultedt(CurrentYear + quarterDay.get(1));
        RP620Dto.setYratescond(results3.get("table1").get(1).get(1)); // 연간누적이용률 보증조건
        RP620Dto.setYratewcond(results3.get("table1").get(1).get(2)); // 연간누적이용률 워런티조건
        RP620Dto.setYraterem(results3.get("table1").get(1).get(3)); // 비고
        RP620Dto.setYeffiscond(results3.get("table1").get(2).get(1));
        RP620Dto.setYeffiwcond(results3.get("table1").get(2).get(2));
        RP620Dto.setYeffirem(results3.get("table1").get(2).get(2));

        /**LTSA 보고서 계약보증조건 및 실적 요약**/
        TB_RP621Dto RP621Dto = TB_RP621DtoSet(results3, dtoValue);

        /**LTSA 보고서 시스템이용률 정보**/
        List<TB_RP622Dto> RP622DtoList = TB_RP622DtoSet(results3, dtoValue);

        // 리소스 정리
        processor.close();



        // 파일이 사용된 후 삭제
        tempFile.deleteOnExit();

    }

    public TB_RP621Dto TB_RP621DtoSet(Map<String, List<List<String>>> result, Map<String, String> dtoValue){

        List<List<String>> resultSet = result.get("table2");

        TB_RP621Dto RP621Dto = new TB_RP621Dto();
        RP621Dto.setSpworkcd(dtoValue.get("spworkcd"));
        RP621Dto.setSpcompcd(dtoValue.get("spcompcd"));
        RP621Dto.setSpplancd(dtoValue.get("spplancd"));
        RP621Dto.setSpworknm(dtoValue.get("spworknm"));
        RP621Dto.setSpworknm(dtoValue.get("spcompnm"));
        RP621Dto.setSpworknm(dtoValue.get("spplannm"));
        RP621Dto.setStandqy(dtoValue.get("Currentyear") + dtoValue.get("CurrentDay"));
        RP621Dto.setYratescond(resultSet.get(2).get(1));
        RP621Dto.setYratesexec(resultSet.get(2).get(2));
        RP621Dto.setYratesrerm(resultSet.get(2).get(3));
        RP621Dto.setYeffiscond(resultSet.get(3).get(1));
        RP621Dto.setYeffisexec(resultSet.get(3).get(2));
        RP621Dto.setYeffisrerm(resultSet.get(3).get(3));
        RP621Dto.setYratewcond(resultSet.get(5).get(0));
        RP621Dto.setYratewexec(resultSet.get(5).get(1));
        RP621Dto.setYratewrerm(resultSet.get(5).get(2));
        RP621Dto.setYeffiwcond(resultSet.get(6).get(0));
        RP621Dto.setYeffiwexec(resultSet.get(6).get(1));
        RP621Dto.setYeffiwrerm(resultSet.get(6).get(2));
        return RP621Dto;
    }

    public List<TB_RP622Dto> TB_RP622DtoSet(Map<String, List<List<String>>> result, Map<String, String> dtoValue){
        List<List<String>> resultSet = result.get("table3");

        List<TB_RP622Dto> RP622Dto = new ArrayList<>();
        List<String> Month = switch (dtoValue.get("CurrentDay")) {
            case "1" -> Arrays.asList("1", "2", "3");
            case "2" -> Arrays.asList("4", "5", "6");
            case "3" -> Arrays.asList("7", "8", "9");
            case "4" -> Arrays.asList("10", "11", "12");
            default -> Arrays.asList("10", "11", "12");
        };

        /*RowHeader.add(0, "1");
        RowHeader.add(1, );
        RowHeader.add(2, dtoValue.get("Currentyear") + Month.get(1));
        RowHeader.add(3, dtoValue.get("Currentyear") + Month.get(3));
        */

        List<String> RowHeader = Arrays.asList("이전누계", dtoValue.get("Currentyear")+ Month.get(0), dtoValue.get("Currentyear")+ Month.get(1),
                dtoValue.get("Currentyear")+ Month.get(2), "분기계", "누계");


        for(int i=1; i <= resultSet.size(); i++){
            TB_RP622Dto dto = new TB_RP622Dto();
            dto.setSpworkcd(dtoValue.get("spworkcd"));
            dto.setSpcompcd(dtoValue.get("spcompcd"));
            dto.setSpplancd(dtoValue.get("spplancd"));
            dto.setSpworknm(dtoValue.get("spworknm"));
            dto.setSpcompnm(dtoValue.get("spcompnm"));
            dto.setSpplannm(dtoValue.get("spplannm"));
            dto.setStandqy(dtoValue.get("Currentyear") + dtoValue.get("CurrentDay"));
            dto.setTermseq(RowHeader.get(i-1));
            dto.setDayscnt(Long.parseLong(resultSet.get(i).get(0)));
            dto.setElecres(Long.parseLong(resultSet.get(i).get(1)));
            dto.setElecres(Long.parseLong(resultSet.get(i).get(2)));
            dto.setElecres(Long.parseLong(resultSet.get(i).get(3)));
            dto.setElecres(Long.parseLong(resultSet.get(i).get(4)));
            dto.setElecres(Long.parseLong(resultSet.get(i).get(5)));
            dto.setRemark(resultSet.get(i).get(6));
            RP622Dto.add(dto);
        }
        return RP622Dto;

    }

    // 파일을 임시 디렉터리에 저장하는 메서드
    private File saveFileToTemp(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("uploaded-", ".pdf"); // 임시 파일 생성
        file.transferTo(tempFile);  // 파일을 임시 파일로 변환
        return tempFile;
    }

    public String extractText(String text, String regStr){
        Pattern pattern = Pattern.compile(regStr);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : "No Found";

    }

    public Map<String, String> extractMultiplePatterns(String text){
        String regStr_ProjectName = "프로젝트명\\s*:\\s*(.*?)(?=\\n|\\r)";
        String regStr_SilJukPeriod = "실적기간\\s*\\s*(.*?)(?=\\n|\\r)";
        String regStr_QuerterAndYear = "보고서\\s*\\s*(.*?)(?=\\n|\\r)";



        Map<String, String> regexPatterns = new HashMap<>();

        regexPatterns.put("프로젝트명", regStr_ProjectName);
        regexPatterns.put("실적기간", regStr_SilJukPeriod);
        regexPatterns.put("보고서", regStr_QuerterAndYear);


        // 결과를 저장할 맵
        Map<String, String> extractedData = new HashMap<>();

        for (Map.Entry<String, String> entry : regexPatterns.entrySet()){
            String key = entry.getKey();
            String regex = entry.getValue();

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            if(matcher.find()){
                extractedData.put(key, matcher.group(1).trim());
            }else{
                extractedData.put(key, "No Found");

            }
        }

        return extractedData;
    }

    public String extractPerformanceGuarantee(String text, String target) {
        // '성능보증' 키워드를 포함하는 문장을 찾는 정규식
        Pattern startPattern = Pattern.compile(target + "\\s*:\\s*(.*?)(?=\\n|\\r)", Pattern.MULTILINE);
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
                String extractedText = text.substring(startIndex, endIndex).trim();

                // 줄바꿈 문자를 공백으로 대체하여 반환
                return extractedText.replaceAll("[\\r\\n]+", " ");
            } else {
                // 다음 항목이 없으면 텍스트 끝까지 추출하고 줄바꿈 문자를 제거
                return text.substring(startIndex).trim().replaceAll("[\\r\\n]+", " ");
            }
        }
        return "성능보증 구간을 찾을 수 없습니다.";
    }
    public List<String> extractYearAndQuarter(String text) {
        List<String> extractedData = new ArrayList<>();

        // '년' 앞의 4자리 숫자를 찾는 정규식
        Pattern yearPattern = Pattern.compile("(\\d{4})년");
        // '분기' 앞의 1자리 숫자를 찾는 정규식
        Pattern quarterPattern = Pattern.compile("(\\d)분기");

        // '년'을 찾고 앞의 4자리 숫자를 추출
        Matcher yearMatcher = yearPattern.matcher(text);
        while (yearMatcher.find()) {
            String year = yearMatcher.group(1);  // 4자리 숫자
            extractedData.add(year);
        }

        // '분기'를 찾고 앞의 1자리 숫자를 추출
        Matcher quarterMatcher = quarterPattern.matcher(text);
        while (quarterMatcher.find()) {
            String quarter = quarterMatcher.group(1);  // 1자리 숫자
            extractedData.add(quarter);
        }

        return extractedData;  // 추출된 데이터를 반환
    }

    public List<String> QuarterToDay(String param){
        List<String> startAndEndDate = new ArrayList<>();
        switch (param){
            case "1": startAndEndDate.add("0101");
            startAndEndDate.add("0331");
            break;
            case "2": startAndEndDate.add("0430");
            startAndEndDate.add("0630");
            break;
            case "3": startAndEndDate.add("0731");
            startAndEndDate.add("0930");
            break;
            case "4" : startAndEndDate.add("1031");
            startAndEndDate.add("1231");

            default: startAndEndDate.add(null);
            startAndEndDate.add(null);
            break;
        }
        return startAndEndDate;
    }
}

