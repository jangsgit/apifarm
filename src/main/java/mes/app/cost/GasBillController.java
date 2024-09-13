package mes.app.cost;


import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP410;
import mes.domain.repository.actasRepository.TB_RP410Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mes.app.cost.service.GasBillService;
import mes.app.cost.service.PdfProcessingService;
import mes.domain.model.AjaxResult;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/cost/gas_bill")
public class GasBillController {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    GasBillService gasBillService;

    @Autowired
    private PdfProcessingService pdfProcessingService;

    @Autowired
    TB_RP410Repository tbRp410Repository;

//    public GasBillController(PdfProcessingService pdfProcessingService) {
//        this.pdfProcessingService = pdfProcessingService;
//    }

    private static final Logger logger = LoggerFactory.getLogger(GasBillController.class);


    // 특정 연도에 대한 1월부터 12월까지의 데이터를 가져오는 API
    @GetMapping("/read")
    public AjaxResult getGasBillList(@RequestParam("year") String year) {
        // 월별 데이터를 가져와서 AjaxResult 형태로 반환
        AjaxResult result = new AjaxResult();

        List<Map<String, Object>> monthlyData = gasBillService.getMonthlyUsageSummary(year);
        result.data = monthlyData;
        return result;

    }

    // 연도 리스트를 가져오는 API
    @GetMapping("/Select_years")
    public List<String> getAvailableYears() {
        return gasBillService.getYear();
    }



    //    pdf 파일 업로드 및 db 저장
    @PostMapping("/upload")
    @Transactional
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file,
                                 @RequestParam("spworkcd") String spworkcd,
                                 @RequestParam("spcompcd") String spcompcd,
                                 @RequestParam("spplannm") String spplannm,
                                 @RequestParam("spworknm") String spworknm,
                                 @RequestParam("spcompnm") String spcompnm,
                                 @RequestParam("spplancd") String spplancd,
                                 Authentication auth) {
        AjaxResult result = new AjaxResult();

        User user = (User) auth.getPrincipal();
        TB_RP410 rp410 = new TB_RP410();
        File tempFile = null;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 파일 유효성 검사
        if (file.isEmpty() || !StringUtils.getFilenameExtension(file.getOriginalFilename()).equalsIgnoreCase("pdf")) {
            logger.warn("파일 업로드 실패: 비어 있거나 PDF 파일이 아닙니다. 파일 이름: {}", file.getOriginalFilename());
            result.success = false;
            result.message = "PDF 파일만 업로드 가능합니다";
            return result;
        }

        try {
            // 업로드된 파일을 임시 위치에 저장
            tempFile = File.createTempFile("uploaded-", ".pdf");
            file.transferTo(tempFile);
            logger.info("파일 업로드 완료, 임시 파일 경로: {}", tempFile.getAbsolutePath());

            // PDF 파일을 처리하고 텍스트를 추출
            List<String> extractedTexts = pdfProcessingService.processPdf(tempFile.getAbsolutePath());
            logger.info("PDF 파일 처리 완료, 추출된 텍스트 개수: {}", extractedTexts.size());

            // OCR 응답 파싱 및 데이터 저장
            boolean isStandymSet = false;
            for (String text : extractedTexts) {
                String parsedText = pdfProcessingService.parseOcrResponse(text);

                // standym 값이 포함된 텍스트를 찾고 변환
                if (!isStandymSet && parsedText.contains("년") && parsedText.contains("월")) {
                    String formattedStandym = convertToStandymFormat(parsedText);
                    if (formattedStandym != null) {
                        rp410.setStandym(formattedStandym);
                        isStandymSet = true; // 중복으로 설정되지 않도록 플래그 설정
                    }
                }


                // ASKAMT 값 처리
                BigDecimal askamt = extractAskamt(parsedText);
                if (askamt != null) {
                    rp410.setAskamt(askamt);
                }
            }


//            rp410.setIndatem(now);

                rp410.setSpworkcd(spworkcd);
                rp410.setSpcompcd(spcompcd);
                rp410.setSpplancd(spplancd);
                rp410.setSpplannm(spplannm);
                rp410.setSpworknm(spworknm);
                rp410.setSpcompnm(spcompnm);

                // 데이터베이스에 엔티티 저장
                rp410 = this.tbRp410Repository.save(rp410);
                logger.info("데이터베이스에 저장된 엔티티: {}", rp410);

                result.success = true;
                result.message = "PDF 파일 처리 및 데이터 저장 완료";
                result.data = rp410;



            // 5. 임시 파일 삭제
            if (tempFile.delete()) {
                logger.info("임시 파일 삭제 완료");
            } else {
                logger.warn("임시 파일 삭제 실패, 파일 경로: {}", tempFile.getAbsolutePath());
            }

            return result;

        } catch (IOException e) {
            logger.error("PDF 파일 처리 중 IOException 발생", e);
            result.success = false;
            result.message = "PDF 파일 처리 중 오류가 발생했습니다.";
        } catch (Exception e) {
            logger.error("예상치 못한 오류 발생", e);
            result.success = false;
            result.message = "예상치 못한 오류가 발생했습니다.";
        } finally {
            // 임시 파일이 존재하는 경우 삭제
            if (tempFile != null && tempFile.exists()) {
                try {
                    tempFile.delete();
                    logger.info("임시 파일 finally 블록에서 삭제 완료");
                } catch (Exception e) {
                    logger.error("finally 블록에서 임시 파일 삭제 중 오류 발생", e);
                }
            }
        }

        return result;
    }


    // 예시: 특정 패턴에 맞는 텍스트를 추출하는 메서드
    private String convertToStandymFormat(String text) {
        // 정규식을 사용하여 연도와 월 추출 (ex. "2023년05월")
        Pattern pattern = Pattern.compile("(\\d{4})년(\\d{2})월");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String year = matcher.group(1);  // 2023
            String month = matcher.group(2); // 05
            return year + month; // "202305"
        }
        return null; // 변환 실패 시 null 반환
    }

    //  ASKAMT 값을 추출하는 메서드
    private BigDecimal extractAskamt(String text) {
        // ASKAMT에 해당하는 값을 추출하는 로직을 작성합니다.
        // 예를 들어, "금액은 92,412,820원 입니다."에서 금액을 추출합니다.
        Pattern pattern = Pattern.compile("금액은\\s*(\\d{1,3}(,\\d{3})*)원"); // 금액 추출을 위한 정규식
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String amountString = matcher.group(1); // 금액 문자열을 가져옴
            // 쉼표 제거 후 BigDecimal로 변환
            String cleanedAmountString = amountString.replaceAll(",", ""); // 쉼표 제거
            return new BigDecimal(cleanedAmountString); // 문자열을 BigDecimal로 변환
        }
        return null; // 값이 없거나 변환 실패 시 null 반환
    }


}








/*  rp410.setStandym(extractedTexts.get(0));
            rp410.setGasuseamt(extractedTexts.get(1));
            rp410.setMetermgamt(extractedTexts.get(2));
            rp410.setImtarramt(extractedTexts.get(3));
            rp410.setSafemgamt(extractedTexts.get(4));
            rp410.setSuppamt(extractedTexts.get(5));
            rp410.setTaxamt(extractedTexts.get(6));
            rp410.setTrunamt(extractedTexts.get(7));
            rp410.setAskamt(extractedTexts.get(8));
            rp410.setUseuamt(extractedTexts.get(9));
            rp410.setSmuseqty(extractedTexts.get(10));
            rp410.setSmusehqty(extractedTexts.get(11));
            // 전월 사용량, 전월 사용열량, 전년 사용량 , 전년 사용열량의 대한 값이 없을수도 있으니 없다면 null 값으로 들어가게 로직 구현
            rp410.setLmuseqty(extractedTexts.size() > 12 ? extractedTexts.get(12) : null);
            rp410.setLmusehqty(extractedTexts.size() > 13 ? extractedTexts.get(13) : null);
            rp410.setLyuseqty(extractedTexts.size() > 14 ? extractedTexts.get(14) : null);
            rp410.setLyusehqty(extractedTexts.size() > 15 ? extractedTexts.get(15) : null);*/

//            String standym = extractedTexts.get(0);    // 년월 저장.
//            String gasuseamt = extractedTexts.get(1); // 가스사용료 저장
//            String metermgamt = extractedTexts.get(2); // 계량기 관리비
//            String imtarramt = extractedTexts.get(3); // 수입부과금환급
//            String safemgamt = extractedTexts.get(4); // 안전관리부담금제외
//            String suppamt = extractedTexts.get(5); // 공급가액
//            String taxamt = extractedTexts.get(6);  // 부가세
//            String trunamt = extractedTexts.get(7); // 절사액
//            String askamt = extractedTexts.get(8);  // 청구금액
//            String useuamt = extractedTexts.get(9); // 사용단가 (이거 pdf에서 못본거같은데 물어봐야함 )
//            String smuseqty = extractedTexts.get(10);   // 당월사용량
//            String smusehqty = extractedTexts.get(11);  // 당월 사용열량
//            String lmuseqty = extractedTexts.size() > 12 ? extractedTexts.get(12) : null;   // 전월사용량
//            String lmusehqty = extractedTexts.size() > 13 ? extractedTexts.get(13) : null;  // 전월사용열량
//            String lyuseqty = extractedTexts.size() > 14 ? extractedTexts.get(14) : null;   // 전년사용량
//            String lyusehqty = extractedTexts.size() > 15 ? extractedTexts.get(15) : null;  // 전년사용열량

