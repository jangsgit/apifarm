package mes.app.actas_inspec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mes.app.actas_inspec.service.HapReportService;
import mes.app.actas_inspec.service.InspecService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.*;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP720Repository;
import mes.domain.repository.TB_RP725Repository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/inspec/hap")
public class HapReportController {

    @Autowired
    HapReportService hapReportService;

    @Autowired
    TB_RP720Repository tb_rp720Repository;

    @Autowired
    TB_RP725Repository tp_rp725Repository;

    @Autowired
    private Settings settings;

    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate,
                              @RequestParam(value = "searchusr", required = false) String searchusr) {
        List<Map<String, Object>> items = new ArrayList<>();

        if (searchusr == null) {
            searchusr = "";
        }

        if (startDate == null) {
            startDate = "";
        }

        if (endDate == null) {
            endDate = "";
        }

        items = this.hapReportService.getList(searchusr, startDate, endDate);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult saveHapReport(@RequestParam Map<String, String> params,
                                    @RequestParam(value = "filelist", required = false) MultipartFile files,
                                    @RequestParam(value = "doc-list", required = false) List<String> doc_list,
                                    Authentication auth) throws IOException {

        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AjaxResult result = new AjaxResult();

        String nspworkcd = params.get("spworkcd");
        String nspcompcd = params.get("spcompcd");
        String nspplancd = params.get("spplancd");
        String ncheckdt = params.get("checkdt").replaceAll("-", "");
        String ncheckno = params.get("checkno");

        TB_RP720_PK pk = new TB_RP720_PK();
        pk.setSpworkcd(nspworkcd);
        pk.setSpcompcd(nspcompcd);
        pk.setSpplancd(nspplancd);
        pk.setCheckdt(ncheckdt);


        if (ncheckno != null && !ncheckno.isEmpty()) {
            pk.setCheckno(ncheckno);
        } else {
            // 점검 순번 유지 또는 생성 로직
            Optional<String> checkseqvalue = tb_rp720Repository.findMaxCheckno(nspworkcd, nspcompcd, nspplancd, ncheckdt);
            String newNo = checkseqvalue.map(s -> String.valueOf(Integer.parseInt(s) + 1)).orElse("1");
            pk.setCheckno(newNo);
        }

        TB_RP720 tbRp720 = new TB_RP720();

        tbRp720.setPk(pk);
        tbRp720.setSpworknm(params.get("spworknm"));
        tbRp720.setSpcompnm(params.get("spcompnm"));
        tbRp720.setSpplannm(params.get("spplannm"));
        tbRp720.setChecknm(params.get("checknm"));
        tbRp720.setCheckusr(params.get("checkusr"));
        tbRp720.setCheckresult(params.get("checkresult"));
        tbRp720.setCheckrem(params.get("checkrem"));
        tbRp720.setCheckarea(params.get("checkarea"));
        tbRp720.setIndatem(now);
        tbRp720.setInuserid(String.valueOf(user.getId()));
        tbRp720.setInusernm(user.getUsername());

        boolean successcode = hapReportService.save(tbRp720, pk, doc_list);

        result.data = tbRp720;


        if (successcode) {
            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }

        return result;
    }

    @PostMapping("/modfind")
    public AjaxResult getById(@RequestBody TB_RP720_PK pk) throws IOException {
        AjaxResult result = new AjaxResult();

        Map<String, Object> item = hapReportService.findById(pk);
        result.data = item;
        return result;
    }

    @DeleteMapping("/delete")
    public AjaxResult deleteElecSafe(@RequestBody List<TB_RP720_PK> pkList) {
        AjaxResult result = new AjaxResult();

        for (TB_RP720_PK pk : pkList) {

            if (pk.getCheckdt() != null) {
                pk.setCheckdt(pk.getCheckdt().replaceAll("-", ""));
            }

            boolean success = hapReportService.delete(pk);

            if (success) {
                result.success = true;
                result.message = "삭제하였습니다.";
            } else {
                result.success = false;
                result.message = "삭제에 실패하였습니다.";
            }
        }
        return result;
    }

    @PostMapping("/download-docs")
    public ResponseEntity<?> downloadDocs(HttpServletResponse response, @RequestBody List<TB_RP720_PK> pkList) throws Exception {
        String path = settings.getProperty("file_upload_path") + "합동점검일지양식.docx";

        if (pkList.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty PK list provided.");
        }

        if (pkList.size() == 1) {
            TB_RP720_PK pk = pkList.get(0);
            FileInputStream fis = new FileInputStream(path);
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFTable> tables = document.getTables();

            Optional<TB_RP720> rp720items = tb_rp720Repository.findById(pk);
            Map<String, Object> item = hapReportService.findById(pk);

            String checknm = null;
            String checkdt = pk.getCheckdt();

            if (rp720items.isPresent()) {
                TB_RP720 rp720item = rp720items.get();
                checknm = rp720item.getChecknm();

                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    List<XWPFRun> runs = paragraph.getRuns();
                    StringBuilder fullText = new StringBuilder();

                    for (XWPFRun run : runs) {
                        if (run.getText(0) != null) {
                            fullText.append(run.getText(0));
                        }
                    }

                    String paragraphText = fullText.toString();

                    if (paragraphText.contains("1. 현장명 :")) {
                        paragraphText = "\n" + paragraphText.replace("1. 현장명 :", "1. 현장명 : " + rp720item.getChecknm());
                    }
                    if (paragraphText.contains("2. 점검일시 :")) {
                        paragraphText = paragraphText.replace("2. 점검일시 :", "2. 점검일시 : " + pkList.get(0).getCheckdt());
                    }

                    if (paragraphText.contains("4. 점검내용")) {
                        paragraphText = "\n" + paragraphText;
                    }
                    if (paragraphText.contains("사진 대지")) {
                        paragraphText = "\n" + paragraphText;
                    }

                    while (paragraph.getRuns().size() > 0) {
                        paragraph.removeRun(0);
                    }

                    String[] lines = paragraphText.split("\n");
                    for (int i = 0; i < lines.length; i++) {
                        XWPFRun newRun = paragraph.createRun();
                        newRun.setText(lines[i], 0);
                        if (i < lines.length - 1) {
                            newRun.addCarriageReturn(); // 줄 바꿈 추가
                        }
                    }
                }

                // 2번째 테이블 작성
                if (tables.size() > 1) {
                    XWPFTable table = tables.get(1);

                    XWPFTableRow row1 = getOrCreateRow(table, 2); // 두 번째 행이 첫 번째 데이터 행
//                    setCellText(row1, 0, "kt");
//                    setCellText(row1, 1, "차장");
                    setCellText(row1, 2, rp720item.getCheckusr());

//                    XWPFTableRow row2 = getOrCreateRow(table, 2); // 세 번째 행이 두 번째 데이터 행
//                    setCellText(row1, 4, "블룸에너지");
//                    setCellText(row1, 5, "과장");
//                    setCellText(row1, 6, "홍길동");
                }

                // 3번째 테이블 작성
                if (tables.size() > 2) {
                    XWPFTable table = tables.get(2);
                    int rowIndex = 1;
                    List<Map<String, Object>> inspectionItems = (List<Map<String, Object>>) item.get("inspectionItems");

                    // inspecnum으로 정렬
                    inspectionItems.sort(Comparator.comparingInt(o -> o.get("inspecnum") != null ? Integer.parseInt(o.get("inspecnum").toString()) : 0));

                    for (Map<String, Object> inspectionItem : inspectionItems) {
                        String inspecresult = inspectionItem.get("inspecresult") != null ? inspectionItem.get("inspecresult").toString() : "";
                        String inspecreform = inspectionItem.get("inspecreform") != null ? inspectionItem.get("inspecreform").toString() : "";

                        XWPFTableRow row = getOrCreateRow(table, rowIndex);
                        setCellText(row, 2, inspecresult);
                        setCellText(row, 3, inspecreform);
                        rowIndex++;
                    }
                }
            }

            ByteArrayOutputStream documentOutputStream = new ByteArrayOutputStream();
            document.write(documentOutputStream);
            document.close();
            fis.close();

            byte[] documentBytes = documentOutputStream.toByteArray();
            ByteArrayInputStream docInputStream = new ByteArrayInputStream(documentBytes);

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docInputStream);
            FOSettings foSettings = Docx4J.createFOSettings();
            foSettings.setWmlPackage(wordMLPackage);

            Mapper fontMapper = new IdentityPlusMapper();
            fontMapper.put("맑은 고딕", PhysicalFonts.get("Malgun Gothic"));
            wordMLPackage.setFontMapper(fontMapper);

            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            Docx4J.toFO(foSettings, pdfOutputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);

            byte[] pdfBytes = pdfOutputStream.toByteArray();
            String pdfName = (checkdt != null ? checkdt : "unknown_date") + "_" + (checknm != null ? checknm : "unknown_name") + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            String encodedFileName = URLEncoder.encode(pdfName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfName + "\"; filename*=UTF-8''" + encodedFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(pdfBytes.length);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } else {
            ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(zipBaos);

            for (TB_RP720_PK pk : pkList) {
                FileInputStream fis = new FileInputStream(path);
                XWPFDocument document = new XWPFDocument(fis);
                List<XWPFTable> tables = document.getTables();

                Optional<TB_RP720> rp720items = tb_rp720Repository.findById(pk);
                Map<String, Object> item = hapReportService.findById(pk);

                String checknm = null;
                String checkdt = pk.getCheckdt();

                if (rp720items.isPresent()) {
                    TB_RP720 rp720item = rp720items.get();
                    checknm = rp720item.getChecknm();

                    for (XWPFParagraph paragraph : document.getParagraphs()) {
                        List<XWPFRun> runs = paragraph.getRuns();
                        StringBuilder fullText = new StringBuilder();

                        for (XWPFRun run : runs) {
                            fullText.append(run.getText(0));
                        }

                        String paragraphText = fullText.toString();

                        if (paragraphText.contains("1. 현장명 :")) {
                            paragraphText = paragraphText.replace("1. 현장명 :", "1. 현장명 : " + rp720item.getChecknm());
                        }
                        if (paragraphText.contains("2. 점검일시 :")) {
                            paragraphText = paragraphText.replace("2. 점검일시 :", "2. 점검일시 : " + pkList.get(0).getCheckdt());
                        }

                        while (paragraph.getRuns().size() > 0) {
                            paragraph.removeRun(0);
                        }

                        String[] parts = paragraphText.split(" ");
                        for (String part : parts) {
                            XWPFRun newRun = paragraph.createRun();
                            newRun.setText(part + " ");
                        }
                    }

                    // 2번째 테이블 작성
                    if (tables.size() > 1) {
                        XWPFTable table = tables.get(1);

                        XWPFTableRow row1 = getOrCreateRow(table, 2); // 두 번째 행이 첫 번째 데이터 행
//                      setCellText(row1, 0, "kt");
//                      setCellText(row1, 1, "차장");
//                      setCellText(row1, 2, rp720item.getCheckusr());

//                      XWPFTableRow row2 = getOrCreateRow(table, 2); // 세 번째 행이 두 번째 데이터 행
//                      setCellText(row1, 4, "블룸에너지");
//                      setCellText(row1, 5, "과장");
//                      setCellText(row1, 6, "홍길동");
                    }

                    // 3번째 테이블 작성
                    if (tables.size() > 2) {
                        XWPFTable table = tables.get(2);
                        int rowIndex = 1;
                        List<Map<String, Object>> inspectionItems = (List<Map<String, Object>>) item.get("inspectionItems");

                        // inspecnum으로 정렬
                        inspectionItems.sort(Comparator.comparingInt(o -> o.get("inspecnum") != null ? Integer.parseInt(o.get("inspecnum").toString()) : 0));

                        for (Map<String, Object> inspectionItem : inspectionItems) {
                            String inspecresult = inspectionItem.get("inspecresult") != null ? inspectionItem.get("inspecresult").toString() : "";
                            String inspecreform = inspectionItem.get("inspecreform") != null ? inspectionItem.get("inspecreform").toString() : "";

                            XWPFTableRow row = getOrCreateRow(table, rowIndex);
                            setCellText(row, 2, inspecresult);
                            setCellText(row, 3, inspecreform);
                            rowIndex++;
                        }
                    }
                }

                ByteArrayOutputStream documentOutputStream = new ByteArrayOutputStream();
                document.write(documentOutputStream);
                document.close();
                fis.close();

                byte[] documentBytes = documentOutputStream.toByteArray();
                ByteArrayInputStream docInputStream = new ByteArrayInputStream(documentBytes);

                WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docInputStream);
                FOSettings foSettings = Docx4J.createFOSettings();
                foSettings.setWmlPackage(wordMLPackage);

                Mapper fontMapper = new IdentityPlusMapper();
                fontMapper.put("맑은 고딕", PhysicalFonts.get("Malgun Gothic"));
                wordMLPackage.setFontMapper(fontMapper);

                ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                Docx4J.toFO(foSettings, pdfOutputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);

                byte[] pdfBytes = pdfOutputStream.toByteArray();
                String pdfName = (checkdt != null ? checkdt : "unknown_date") + "_" + (checknm != null ? checknm : "unknown_name") + ".pdf";

                zipOut.putNextEntry(new ZipEntry(pdfName));
                zipOut.write(pdfBytes);
                zipOut.closeEntry();
            }

            zipOut.close();

            ByteArrayResource zipResource = new ByteArrayResource(zipBaos.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            String encodedZipFileName = URLEncoder.encode("합동점검일지.zip", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "합동점검일지.zip" + "\"; filename*=UTF-8''" + encodedZipFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(zipResource.contentLength());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipResource);
        }
    }

    // 지정된 인덱스에 행이 존재하지 않으면 새 행을 생성
    private XWPFTableRow getOrCreateRow(XWPFTable table, int index) {
        while (table.getRows().size() <= index) {
            table.createRow();
        }
        return table.getRow(index);
    }

    // 지정된 행의 셀에 텍스트를 설정
    private void setCellText(XWPFTableRow row, int cellIndex, String text) {
        XWPFTableCell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell();
        }
        cell.setText(text);
    }

}
