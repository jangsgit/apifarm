package mes.app.actas_inspec;

import com.fasterxml.jackson.databind.ObjectMapper;
import mes.app.actas_inspec.service.HapReportService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.*;
import mes.domain.model.AjaxResult;
import mes.domain.repository.actasRepository.TB_RP720Repository;
import mes.domain.repository.actasRepository.TB_RP725Repository;
import mes.domain.repository.actasRepository.TB_RP726Repository;
import mes.domain.repository.actasRepository.TB_INSPECRepository;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/inspec/hap")
public class HapReportController {

    @Autowired
    HapReportService hapReportService;

    @Autowired
    TB_RP720Repository TBRP720Repository;

    @Autowired
    TB_RP725Repository TBRP725Repository;

    @Autowired
    TB_RP726Repository TBRP726Repository;

    @Autowired
    private Settings settings;

    @Autowired
    TB_INSPECRepository tb_inspecRepository;

    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate,
                              @RequestParam(value = "searchusr", required = false) String searchusr,
                              @RequestParam(value = "searchcom", required = false) String searchcom,
                              @RequestParam(value = "spworkcd") String spworkcd,
                              @RequestParam(value = "spcompcd") String spcompcd,
                              @RequestParam(value = "spplancd") String spplancd) {
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

        String c_startDate = startDate.replaceAll("-", "");
        String c_endDate = endDate.replaceAll("-", "");

        items = this.hapReportService.getList(searchusr, c_startDate, c_endDate, spworkcd, spcompcd, spplancd, searchcom);

        // 각 항목의 endresult 값을 변환
        for (Map<String, Object> item : items) {
            // 날짜 형식 변환 (checkdt)
            if (item.containsKey("checkdt")) {
                String checkdt = (String) item.get("checkdt");
                if (checkdt != null && checkdt.length() == 8) {
                    String formattedDate = checkdt.substring(0, 4) + "-" + checkdt.substring(4, 6) + "-" + checkdt.substring(6, 8);
                    item.put("checkdt", formattedDate);
                }
            }

            // 파일 이름과 경로를 리스트로 변환
            if (item.containsKey("filenames") && item.containsKey("filepaths")) {
                String filenames = (String) item.get("filenames");
                String filepaths = (String) item.get("filepaths");

                List<String> filenameList = filenames != null ? Arrays.asList(filenames.split(",")) : Collections.emptyList();
                List<String> filepathList = filepaths != null ? Arrays.asList(filepaths.split(",")) : Collections.emptyList();

                item.put("filenameList", filenameList);
                item.put("filepathList", filepathList);
                item.put("isdownload", !filenameList.isEmpty() && !filepathList.isEmpty());
            } else {
                item.put("filenameList", Collections.emptyList());
                item.put("filepathList", Collections.emptyList());
                item.put("isdownload", false);
            }
        }

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult saveHapReport(@RequestParam Map<String, String> params,
                                    @RequestParam(value = "filelist", required = false) MultipartFile[] files,
                                    @RequestPart(value = "deletedFiles", required = false) MultipartFile[] deletedFiles,
                                    @RequestParam(value = "doc-list", required = false) List<String> doc_list,
                                    @RequestParam(value = "deletedSeqs", required = false) List<Integer> deletedSeqs,
                                    Authentication auth) throws IOException {

        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());

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

        String finalCheckno;
        if (ncheckno != null && !ncheckno.isEmpty()) {
            pk.setCheckno(ncheckno);
            finalCheckno = ncheckno;
        } else {
            // 점검 순번 유지 또는 생성 로직
            Optional<String> checkseqvalue = TBRP720Repository.findMaxCheckno(nspworkcd, nspcompcd, nspplancd, ncheckdt);
            String newSeq = checkseqvalue.map(s -> String.valueOf(Integer.parseInt(s) + 1)).orElse("1");
            pk.setCheckno(newSeq);
            finalCheckno = newSeq;
        }

        TB_RP720 TBRP720 = new TB_RP720();
        TBRP720.setId(pk);
        TBRP720.setSpworknm(params.get("spworknm"));
        TBRP720.setSpcompnm(params.get("spcompnm"));
        TBRP720.setSpplannm(params.get("spplannm"));
        TBRP720.setChecknm(params.get("checknm"));
        TBRP720.setCheckrem(params.get("checkrem"));
        TBRP720.setChkaddres(params.get("chkaddres"));
        TBRP720.setIndatem(now);
        TBRP720.setInusernm(user.getFirst_name());
        TBRP720.setInuserid(user.getUsername());

        AjaxResult result = new AjaxResult();

        boolean success = hapReportService.save(TBRP720);
        if (!success) {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
            return result;
        }

        // 삭제된 파일 처리
        if (deletedFiles != null && deletedFiles.length > 0) {
            List<TB_RP725> tbRp725List = new ArrayList<>();
            for (MultipartFile deletedFile : deletedFiles) {
                String content = new String(deletedFile.getBytes(), StandardCharsets.UTF_8);
                Map<String, String> deletedFileMap = new ObjectMapper().readValue(content, Map.class);

                String spworkcd = deletedFileMap.get("spworkcd");
                String spcompcd = deletedFileMap.get("spcompcd");
                String spplancd = deletedFileMap.get("spplancd");
                String checkdt = deletedFileMap.get("checkdt");
                String checkno = deletedFileMap.get("checkno");
                String checkseq = deletedFileMap.get("checkseq");

                TB_RP725_PK pk2 = new TB_RP725_PK(spworkcd, spcompcd, spplancd, checkdt, checkno, checkseq);
                TB_RP725 tbRp725 = TBRP725Repository.findById(pk2).orElse(null);
                if (tbRp725 != null) {
                    // 파일 삭제
                    String filePath = tbRp725.getFilepath();
                    String fileName = tbRp725.getFilesvnm();
                    File file = new File(filePath, fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    tbRp725List.add(tbRp725);
                }
            }
            TBRP725Repository.deleteAll(tbRp725List);
        }

        // 파일 시퀀스 생성 로직
        Optional<String> maxFileseqValue = TBRP725Repository.findMaxCheckseq(nspworkcd, nspcompcd, nspplancd, ncheckdt, finalCheckno);
        String newCheckSeq = maxFileseqValue.map(s -> String.valueOf(Integer.parseInt(s) + 1)).orElse("1");

        // 파일 처리
        if (files != null) {
            List<TB_RP725> tbRp725List = new ArrayList<>();
            for (MultipartFile multipartFile : files) {

                TB_RP725_PK pk2 = new TB_RP725_PK(nspworkcd, nspcompcd, nspplancd, ncheckdt, finalCheckno, newCheckSeq);
                int newCheckSeqInt = Integer.parseInt(newCheckSeq);
                newCheckSeqInt++;
                newCheckSeq = String.valueOf(newCheckSeqInt);

                TB_RP725 TBRP725 = new TB_RP725();
                TBRP725.setId(pk2);
                TBRP725.setSpworknm(params.get("spworknm"));
                TBRP725.setSpcompnm(params.get("spcompnm"));
                TBRP725.setSpplannm(params.get("spplannm"));

                String path = settings.getProperty("file_upload_path") + "합동안전점검";
                MultipartFile file = multipartFile;
                String fileName = file.getOriginalFilename();
                String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                String file_uuid_name = UUID.randomUUID().toString() + "." + ext;
                String saveFilePath = path;
                File saveDir = new File(saveFilePath);
                MultipartFile mFile = null;

                mFile = file;
                float fileSize = (float) file.getSize();

                //디렉토리 없으면 생성
                if (!saveDir.isDirectory()) {
                    saveDir.mkdirs();
                }

                File saveFile = new File(path + File.separator + file_uuid_name);
                mFile.transferTo(saveFile);

                TBRP725.setFilepath(saveFilePath);
                TBRP725.setFilesvnm(file_uuid_name);
                TBRP725.setFileextns(ext);
                TBRP725.setFileurl(saveFilePath);
                TBRP725.setFileornm(fileName);
                TBRP725.setFilesize(fileSize);
                TBRP725.setIndatem(now);
                TBRP725.setInusernm(user.getFirst_name());
                TBRP725.setInuserid(user.getUsername());

                // 리스트에 추가
                tbRp725List.add(TBRP725);
            }
            try {
                TBRP725Repository.saveAll(tbRp725List); // saveAll() 메서드 호출
                // 성공 시
                result.success = true;
                result.message = "파일 저장에 성공하였습니다.";
            } catch (Exception e) {
                // 실패 시 예외 처리
                result.success = false;
                result.message = "파일 저장에 실패하였습니다.";
                e.printStackTrace(); // 디버깅을 위한 예외 출력
                return result;
            }
        }

        // doc 처리
        List<String> divisionList = new ArrayList<>();
        List<String> contList = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        List<String> reformList = new ArrayList<>();
        List<Integer> seqList = new ArrayList<>();

        for (String doc : doc_list) {
            String[] parts = doc.split("@", -1);

            if (parts.length >= 5) {
                Integer seq = parts[0].trim().isEmpty() ? null : Integer.parseInt(parts[0].trim());
                seqList.add(seq);
                divisionList.add(parts[1].trim());
                contList.add(parts[2].trim());
                resultList.add(parts[3].trim());
                reformList.add(parts[4].trim());
            }
        }

        for (int i = 0; i < doc_list.size(); i++) {
            TB_INSPEC tb_inspec;

            if (seqList.get(i) != null) {
                // seq 값이 존재하는 경우 기존 객체를 가져와 업데이트
                Optional<TB_INSPEC> existingInspec = tb_inspecRepository.findBySeq(seqList.get(i));
                if (existingInspec.isPresent()) {
                    tb_inspec = existingInspec.get();
                } else {
                    // seq 값이 존재하지만 해당 seq에 해당하는 데이터가 없을 경우 새로 생성
                    tb_inspec = new TB_INSPEC();
                    tb_inspec.setSeq(seqList.get(i));
                }
            } else {
                // seq 값이 존재하지 않는 경우 새로 생성
                int maxSeq;
                Optional<Integer> seqValue = tb_inspecRepository.findTopByOrderBySeqDesc();
                maxSeq = seqValue.orElse(0);  // MaxSeq를 0으로 초기화
                tb_inspec = new TB_INSPEC();
                tb_inspec.setSeq(maxSeq + 1);
            }

            tb_inspec.setSpworkcd(pk.getSpworkcd());
            tb_inspec.setSpworknm(TBRP720.getSpworknm());
            tb_inspec.setSpcompcd(pk.getSpcompcd());
            tb_inspec.setSpcompnm(TBRP720.getSpcompnm());
            tb_inspec.setSpplancd(pk.getSpplancd());
            tb_inspec.setSpplannm(TBRP720.getSpplannm());
            tb_inspec.setTabletype("TB_RP720");
            tb_inspec.setInspecnum(i + 1);
            tb_inspec.setInspecdivision(divisionList.get(i));
            tb_inspec.setInspeccont(contList.get(i));
            tb_inspec.setInspecresult(resultList.get(i));
            tb_inspec.setInspecreform(reformList.get(i));
            tb_inspec.setCheckdt(pk.getCheckdt());
            tb_inspec.setCheckno(pk.getCheckno());
            try {
                tb_inspecRepository.save(tb_inspec);
            } catch (Exception e) {
                result.success = false;
                result.message = "점검사항 저장에 실패하였습니다: " + e.getMessage();
                return result;
            }

        }

        // 점검자 처리 (checkusr1)
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();

            // checkusr1_company_로 시작하는 키들만 처리
            if (key.startsWith("checkusr1_company_")) {
                // 인덱스 추출 (checkusr1_company_ 뒤에 숫자 부분)
                String indexStr = key.substring("checkusr1_company_".length());
                int index = Integer.parseInt(indexStr);

                // 해당 인덱스에 있는 다른 값들도 가져옴
                String seq1 = params.get("checkusr1_seq_" + index);
                String company1 = params.get("checkusr1_company_" + index);
                String position1 = params.get("checkusr1_position_" + index);
                String name1 = params.get("checkusr1_name_" + index);

                // 값이 있는 경우에만 처리
                if (company1 != null && !company1.isEmpty() &&
                        position1 != null && !position1.isEmpty() &&
                        name1 != null && !name1.isEmpty()) {

                    TB_RP726 inspector1;
                    if (seq1 != null && !seq1.isEmpty()) {
                        inspector1 = TBRP726Repository.findById(Integer.parseInt(seq1)).orElse(new TB_RP726());
                    } else {
                        inspector1 = new TB_RP726();
                    }

                    inspector1.setSpworkcd(pk.getSpworkcd());
                    inspector1.setSpcompcd(pk.getSpcompcd());
                    inspector1.setSpplancd(pk.getSpplancd());
                    inspector1.setSpmenu("tbRp720");
                    inspector1.setCheckdt(pk.getCheckdt());
                    inspector1.setCheckno(pk.getCheckno());
                    inspector1.setChkflag("0");
                    inspector1.setCompany(company1);
                    inspector1.setJiggeub(position1);
                    inspector1.setCheckusr(name1);

                    try {
                        TBRP726Repository.save(inspector1);
                    } catch (Exception e) {
                        result.success = false;
                        result.message = "도급인 저장에 실패하였습니다: " + e.getMessage();
                        return result;
                    }
                }
            }
        }

        // 점검자 처리 (checkusr2)도 동일한 방식으로 처리
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();

            // checkusr2_company_로 시작하는 키들만 처리
            if (key.startsWith("checkusr2_company_")) {
                // 인덱스 추출 (checkusr2_company_ 뒤에 숫자 부분)
                String indexStr = key.substring("checkusr2_company_".length());
                int index = Integer.parseInt(indexStr);

                // 해당 인덱스에 있는 다른 값들도 가져옴
                String seq2 = params.get("checkusr2_seq_" + index);
                String company2 = params.get("checkusr2_company_" + index);
                String position2 = params.get("checkusr2_position_" + index);
                String name2 = params.get("checkusr2_name_" + index);

                // 값이 있는 경우에만 처리
                if (company2 != null && !company2.isEmpty() &&
                        position2 != null && !position2.isEmpty() &&
                        name2 != null && !name2.isEmpty()) {

                    TB_RP726 inspector2;
                    if (seq2 != null && !seq2.isEmpty()) {
                        inspector2 = TBRP726Repository.findById(Integer.parseInt(seq2)).orElse(new TB_RP726());
                    } else {
                        inspector2 = new TB_RP726();
                    }

                    inspector2.setSpworkcd(pk.getSpworkcd());
                    inspector2.setSpcompcd(pk.getSpcompcd());
                    inspector2.setSpplancd(pk.getSpplancd());
                    inspector2.setSpmenu("tbRp720");
                    inspector2.setCheckdt(pk.getCheckdt());
                    inspector2.setCheckno(pk.getCheckno());
                    inspector2.setChkflag("1");
                    inspector2.setCompany(company2);
                    inspector2.setJiggeub(position2);
                    inspector2.setCheckusr(name2);

                    try {
                        TBRP726Repository.save(inspector2);
                    } catch (Exception e) {
                        result.success = false;
                        result.message = "관계수급인 저장에 실패하였습니다: " + e.getMessage();
                        return result;
                    }
                }
            }
        }

        // 삭제된 점검자 처리
        if (deletedSeqs != null && !deletedSeqs.isEmpty()) {
            for (Integer seq : deletedSeqs) {
                try {
                    TBRP726Repository.deleteById(seq);
                } catch (Exception e) {
                    result.success = false;
                    result.message = "삭제된 점검자 처리에 실패하였습니다.";
                    return result;
                }
            }
        }

        result.success = true;
        result.message = "저장하였습니다.";
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
    public ResponseEntity<?> downloadDocs(HttpServletResponse response, @RequestBody List<TB_RP720_PK> pkList) throws
            Exception {
        String path = settings.getProperty("file_upload_path") + "합동점검일지양식.docx";

        if (pkList.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty PK list provided.");
        }

        if (pkList.size() == 1) {
            TB_RP720_PK pk = pkList.get(0);
            FileInputStream fis = new FileInputStream(path);
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFTable> tables = document.getTables();

            Map<String, Object> item = hapReportService.findById(pk);

            String checknm = (String) item.get("checknm");
            String checkdt = pk.getCheckdt();


            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String formattedCheckdt = formatDateStringToKorean(checkdt);
                List<XWPFRun> runs = paragraph.getRuns();
                StringBuilder fullText = new StringBuilder();

                for (XWPFRun run : runs) {
                    if (run.getText(0) != null) {
                        fullText.append(run.getText(0));
                    }
                }

                String paragraphText = fullText.toString();

                if (paragraphText.contains("1. 현장명 :")) {
                    paragraphText = "\n" + paragraphText.replace("1. 현장명 :", "1. 현장명 : " + checknm);
                }
                if (paragraphText.contains("2. 점검일시 :")) {
                    paragraphText = paragraphText.replace("2. 점검일시 :", "2. 점검일시 : " + formattedCheckdt);
                }

                if (paragraphText.contains("4. 점검내용")) {
                    paragraphText = "\n" + paragraphText;
                }
                if (paragraphText.contains("사진 대지")) {
                    paragraphText = paragraphText.replace("사진 대지", "[PAGE_BREAK]사진 대지");
                }

                while (paragraph.getRuns().size() > 0) {
                    paragraph.removeRun(0);
                }

                String[] lines = paragraphText.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    XWPFRun newRun = paragraph.createRun();

                    if (lines[i].contains("[PAGE_BREAK]")) {
                        // 페이지 나누기 실행
                        newRun.addBreak(BreakType.PAGE);
                        lines[i] = lines[i].replace("[PAGE_BREAK]", "");
                    }

                    newRun.setText(lines[i], 0);
                    if (i < lines.length - 1) {
                        newRun.addCarriageReturn(); // 줄 바꿈 추가
                    }
                }
            }

            // 2번째 테이블 작성
            if (tables.size() > 1) {
                XWPFTable table = tables.get(1);
                List<Map<String, Object>> inspectorList = (List<Map<String, Object>>) item.get("inspectorlist");

                int rowIndex1 = 2;  // 도급인 데이터를 입력할 행 번호
                int rowIndex2 = 2;  // 관계수급인 데이터를 입력할 행 번호

                for (Map<String, Object> inspector : inspectorList) {
                    String company = inspector.get("company") != null ? inspector.get("company").toString() : "";
                    String jiggeub = inspector.get("jiggeub") != null ? inspector.get("jiggeub").toString() : "";
                    String checkusr = inspector.get("checkusr") != null ? inspector.get("checkusr").toString() : "";
                    String chkflag = inspector.get("chkflag") != null ? inspector.get("chkflag").toString() : "";

                    XWPFTableRow row;

                    if ("0".equals(chkflag)) {
                        // 도급인 (0)
                        row = getOrCreateRow(table, rowIndex1);  // 도급인 행 생성
                        setCellText(row, 0, company, rowIndex1, 0);  // 회사
                        setCellText(row, 1, jiggeub, rowIndex1, 1);  // 직급
                        setCellText(row, 2, checkusr, rowIndex1, 2); // 성명
                        rowIndex1++;
                    } else if ("1".equals(chkflag)) {
                        // 관계수급인 (1)
                        row = getOrCreateRow(table, rowIndex2);  // 관계수급인 행 생성
                        setCellText(row, 4, company, rowIndex2, 4);  // 회사
                        setCellText(row, 5, jiggeub, rowIndex2, 5);  // 직급
                        setCellText(row, 6, checkusr, rowIndex2, 6); // 성명
                        rowIndex2++;
                    }

                }
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
                    setCellText(row, 2, inspecresult, rowIndex, 2);
                    setCellText(row, 3, inspecreform, rowIndex, 3);
                    rowIndex++;
                }
            }

            // 4번째 테이블 작성
            if (tables.size() > 3) {
                int tableIndex = 3;  // 네 번째 테이블부터 시작
                XWPFTable table = tables.get(tableIndex); // 네 번째 테이블 선택
                List<Map<String, Object>> filelist = (List<Map<String, Object>>) item.get("filelist");

                // checkdt 값 포맷팅 (예: 20240827 -> 2024년 8월)
                String formattedCheckdt = formatDateStringToKoreanYearMonth(checkdt);  // 2024년 8월
                String formattedCheckdtWithDots = formatDateStringWithDots(checkdt);   // 2024.08.27

                int rowIndex = 0;  // 현재 행 인덱스

                for (int i = 0; i < filelist.size(); i++) {
                    Map<String, Object> file = filelist.get(i);
                    String filepath = file.get("filepath") != null ? file.get("filepath").toString() : "";
                    String filesvnm = file.get("filesvnm") != null ? file.get("filesvnm").toString() : "";
                    String fileornm = file.get("fileornm") != null ? file.get("fileornm").toString() : "";
                    String fileNameWithoutExt = fileornm.contains(".") ? fileornm.substring(0, fileornm.lastIndexOf('.')) : fileornm;

                    // 전체 파일 경로를 생성
                    String fullFilePath = filepath + File.separator + filesvnm;

                    // 첫 번째 행 - 이미지 삽입
                    XWPFTableRow imageRow;
                    if (table.getRows().size() > rowIndex) {
                        imageRow = table.getRow(rowIndex);
                    } else {
                        XWPFTableRow templateImageRow = table.getRow(0); // 첫 번째 행을 템플릿으로 사용
                        imageRow = table.createRow();
                        copyRowStyle(templateImageRow, imageRow); // 행 스타일 복사
                    }
                    insertImageIntoCell(imageRow.getCell(0), fullFilePath);  // 파일 경로를 사용하여 이미지 삽입

                    // 두 번째 행 - 텍스트 추가
                    XWPFTableRow contentRow;
                    if (table.getRows().size() > rowIndex + 1) {
                        contentRow = table.getRow(rowIndex + 1);
                    } else {
                        XWPFTableRow templateContentRow = table.getRow(1); // 두 번째 행을 템플릿으로 사용
                        contentRow = table.createRow();
                        copyRowStyle(templateContentRow, contentRow); // 행 스타일 복사
                        setCellText(contentRow, 0, "내용", 1, 0);  // "내용" 텍스트 추가
                    }
                    setCellText(contentRow, 1, formattedCheckdt + " 합동점검", 1, 1);

                    // 세 번째 행 - 파일명 및 날짜 추가
                    XWPFTableRow infoRow;
                    if (table.getRows().size() > rowIndex + 2) {
                        infoRow = table.getRow(rowIndex + 2);
                    } else {
                        XWPFTableRow templateInfoRow = table.getRow(2); // 세 번째 행을 템플릿으로 사용
                        infoRow = table.createRow();
                        copyRowStyle(templateInfoRow, infoRow); // 행 스타일 복사
                        setCellText(infoRow, 0, "장소", 2, 0);  // "장소" 텍스트 추가
                        setCellText(infoRow, 2, "일시", 2, 2);  // "일시" 텍스트 추가
                    }
                    setCellText(infoRow, 1, fileNameWithoutExt, 2, 1);  // 파일명 (확장자 제거)
                    setCellText(infoRow, 3, formattedCheckdtWithDots, 2, 3);  // 일시 (yyyy.MM.dd)

                    // 다음 묶음을 위해 행 인덱스를 3 증가
                    rowIndex += 3;
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

            for (int i = 0; i < pkList.size(); i++) {
                TB_RP720_PK pk = pkList.get(i);
                FileInputStream fis = new FileInputStream(path);
                XWPFDocument document = new XWPFDocument(fis);
                List<XWPFTable> tables = document.getTables();

                Map<String, Object> item = hapReportService.findById(pk);

                String checknm = (String) item.get("checknm");
                String checkdt = pk.getCheckdt();


                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    String formattedCheckdt = formatDateStringToKorean(checkdt);
                    List<XWPFRun> runs = paragraph.getRuns();
                    StringBuilder fullText = new StringBuilder();

                    for (XWPFRun run : runs) {
                        if (run.getText(0) != null) {
                            fullText.append(run.getText(0));
                        }
                    }

                    String paragraphText = fullText.toString();

                    if (paragraphText.contains("1. 현장명 :")) {
                        paragraphText = "\n" + paragraphText.replace("1. 현장명 :", "1. 현장명 : " + checknm);
                    }
                    if (paragraphText.contains("2. 점검일시 :")) {
                        paragraphText = paragraphText.replace("2. 점검일시 :", "2. 점검일시 : " + formattedCheckdt);
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
                    for (int j = 0; j < lines.length; j++) {
                        XWPFRun newRun = paragraph.createRun();
                        newRun.setText(lines[j], 0);
                        if (j < lines.length - 1) {
                            newRun.addCarriageReturn(); // 줄 바꿈 추가
                        }
                    }
                }

                // 2번째 테이블 작성
                if (tables.size() > 1) {
                    XWPFTable table = tables.get(1);
                    List<Map<String, Object>> inspectorList = (List<Map<String, Object>>) item.get("inspectorlist");

                    int rowIndex1 = 2;  // 도급인 데이터를 입력할 행 번호
                    int rowIndex2 = 2;  // 관계수급인 데이터를 입력할 행 번호

                    for (Map<String, Object> inspector : inspectorList) {
                        String company = inspector.get("company") != null ? inspector.get("company").toString() : "";
                        String jiggeub = inspector.get("jiggeub") != null ? inspector.get("jiggeub").toString() : "";
                        String checkusr = inspector.get("checkusr") != null ? inspector.get("checkusr").toString() : "";
                        String chkflag = inspector.get("chkflag") != null ? inspector.get("chkflag").toString() : "";

                        XWPFTableRow row;

                        if ("0".equals(chkflag)) {
                            // 도급인 (0)
                            row = getOrCreateRow(table, rowIndex1);  // 도급인 행 생성
                            setCellText(row, 0, company, rowIndex1, 0);  // 회사
                            setCellText(row, 1, jiggeub, rowIndex1, 1);  // 직급
                            setCellText(row, 2, checkusr, rowIndex1, 2); // 성명
                            rowIndex1++;
                        } else if ("1".equals(chkflag)) {
                            // 관계수급인 (1)
                            row = getOrCreateRow(table, rowIndex2);  // 관계수급인 행 생성
                            setCellText(row, 4, company, rowIndex2, 4);  // 회사
                            setCellText(row, 5, jiggeub, rowIndex2, 5);  // 직급
                            setCellText(row, 6, checkusr, rowIndex2, 6); // 성명
                            rowIndex2++;
                        }

                    }
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
                        setCellText(row, 2, inspecresult, rowIndex, 2);
                        setCellText(row, 3, inspecreform, rowIndex, 3);
                        rowIndex++;
                    }
                }

                // 4번째 테이블 작성
                if (tables.size() > 3) {
                    int tableIndex = 3;  // 네 번째 테이블부터 시작
                    XWPFTable table = tables.get(tableIndex); // 네 번째 테이블 선택
                    List<Map<String, Object>> filelist = (List<Map<String, Object>>) item.get("filelist");

                    // checkdt 값 포맷팅 (예: 20240827 -> 2024년 8월)
                    String formattedCheckdt = formatDateStringToKoreanYearMonth(checkdt);  // 2024년 8월
                    String formattedCheckdtWithDots = formatDateStringWithDots(checkdt);   // 2024.08.27

                    int rowIndex = 0;  // 현재 행 인덱스

                    for (int k= 0; k < filelist.size(); k++) {
                        Map<String, Object> file = filelist.get(k);
                        String filepath = file.get("filepath") != null ? file.get("filepath").toString() : "";
                        String filesvnm = file.get("filesvnm") != null ? file.get("filesvnm").toString() : "";
                        String fileornm = file.get("fileornm") != null ? file.get("fileornm").toString() : "";
                        String fileNameWithoutExt = fileornm.contains(".") ? fileornm.substring(0, fileornm.lastIndexOf('.')) : fileornm;

                        // 전체 파일 경로를 생성
                        String fullFilePath = filepath + File.separator + filesvnm;

                        // 첫 번째 행 - 이미지 삽입
                        XWPFTableRow imageRow;
                        if (table.getRows().size() > rowIndex) {
                            imageRow = table.getRow(rowIndex);
                        } else {
                            XWPFTableRow templateImageRow = table.getRow(0); // 첫 번째 행을 템플릿으로 사용
                            imageRow = table.createRow();
                            copyRowStyle(templateImageRow, imageRow); // 행 스타일 복사
                        }
                        insertImageIntoCell(imageRow.getCell(0), fullFilePath);  // 파일 경로를 사용하여 이미지 삽입

                        // 두 번째 행 - 텍스트 추가
                        XWPFTableRow contentRow;
                        if (table.getRows().size() > rowIndex + 1) {
                            contentRow = table.getRow(rowIndex + 1);
                        } else {
                            XWPFTableRow templateContentRow = table.getRow(1); // 두 번째 행을 템플릿으로 사용
                            contentRow = table.createRow();
                            copyRowStyle(templateContentRow, contentRow); // 행 스타일 복사
                            setCellText(contentRow, 0, "내용", 1, 0);  // "내용" 텍스트 추가
                        }
                        setCellText(contentRow, 1, formattedCheckdt + " 합동점검", 1, 1);

                        // 세 번째 행 - 파일명 및 날짜 추가
                        XWPFTableRow infoRow;
                        if (table.getRows().size() > rowIndex + 2) {
                            infoRow = table.getRow(rowIndex + 2);
                        } else {
                            XWPFTableRow templateInfoRow = table.getRow(2); // 세 번째 행을 템플릿으로 사용
                            infoRow = table.createRow();
                            copyRowStyle(templateInfoRow, infoRow); // 행 스타일 복사
                            setCellText(infoRow, 0, "장소", 2, 0);  // "장소" 텍스트 추가
                            setCellText(infoRow, 2, "일시", 2, 2);  // "일시" 텍스트 추가
                        }
                        setCellText(infoRow, 1, fileNameWithoutExt, 2, 1);  // 파일명 (확장자 제거)
                        setCellText(infoRow, 3, formattedCheckdtWithDots, 2, 3);  // 일시 (yyyy.MM.dd)

                        // 다음 묶음을 위해 행 인덱스를 3 증가
                        rowIndex += 3;
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
                // 중복을 방지하기 위해 파일 이름에 인덱스 추가
                // pdfNameBase에 인덱스를 추가하여 중복 방지
                String pdfNameBase = (checkdt != null ? checkdt : "unknown_date") + "_" + (checknm != null ? checknm : "unknown_name");
                String pdfName = pdfNameBase + "_" + (i + 1) + ".pdf"; // _1, _2, _3 ... 추가

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

    // 지정된 인덱스에 행이 존재하지 않으면 새 행을 생성하고, 스타일을 적용
    private XWPFTableRow getOrCreateRow(XWPFTable table, int index) {
        XWPFTableRow row;
        if (table.getRows().size() <= index) {
            row = table.createRow();
            // 스타일 적용
            copyRowStyle(table.getRow(index - 1), row);
        } else {
            row = table.getRow(index);
        }
        return row;
    }

    // setCellText 메서드 수정 (기존 셀 스타일 복사)
    private void setCellText(XWPFTableRow row, int cellIndex, String text, int rowIndex, int templateCellIndex) {
        XWPFTableCell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell();
            // 스타일 적용
            copyCellStyle(row.getTable().getRow(rowIndex - 1).getCell(templateCellIndex), cell);
        }
        cell.setText(text);
    }

    // 행 스타일을 복사하는 메서드
    private void copyRowStyle(XWPFTableRow templateRow, XWPFTableRow newRow) {
        newRow.getCtRow().setTrPr(templateRow.getCtRow().getTrPr());
        for (int i = 0; i < templateRow.getTableCells().size(); i++) {
            XWPFTableCell templateCell = templateRow.getCell(i);
            XWPFTableCell newCell = newRow.getCell(i);
            if (newCell == null) {
                newCell = newRow.createCell();
            }
            copyCellStyle(templateCell, newCell);
        }
    }

    // 셀 스타일을 복사하는 메서드
    private void copyCellStyle(XWPFTableCell templateCell, XWPFTableCell newCell) {
        if (templateCell != null && newCell != null) {
            // 텍스트와 문단 스타일 복사
            newCell.getCTTc().setTcPr(templateCell.getCTTc().getTcPr());
            for (int i = 0; i < templateCell.getParagraphs().size(); i++) {
                XWPFParagraph templateParagraph = templateCell.getParagraphArray(i);
                XWPFParagraph newParagraph = newCell.getParagraphArray(i);
                if (newParagraph == null) {
                    newParagraph = newCell.addParagraph();
                }
                newParagraph.getCTP().setPPr(templateParagraph.getCTP().getPPr());
            }
        }
    }

    // 날짜 문자열을 yyyyMMdd 형식에서 yyyy년 M월 d일 형식으로 변환하는 메서드
    private String formatDateStringToKorean(String dateStr) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy년 M월 d일");
            Date date = originalFormat.parse(dateStr);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // 오류 발생 시 원래 날짜 문자열을 반환
        }
    }

    // 날짜 문자열을 yyyyMMdd 형식에서 yyyy년 M월 형식으로 변환하는 메서드
    private String formatDateStringToKoreanYearMonth(String dateStr) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy년 M월");
            Date date = originalFormat.parse(dateStr);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // 오류 발생 시 원래 날짜 문자열을 반환
        }
    }

    // 날짜 문자열을 yyyyMMdd 형식에서 yyyy.MM.dd 형식으로 변환하는 메서드
    private String formatDateStringWithDots(String dateStr) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy.MM.dd");
            Date date = originalFormat.parse(dateStr);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // 오류 발생 시 원래 날짜 문자열을 반환
        }
    }

    // 셀에 이미지 삽입하는 메서드 (이미지 경로 사용)
    private void insertImageIntoCell(XWPFTableCell cell, String imagePath) {
        try (InputStream is = new FileInputStream(imagePath)) {
            XWPFParagraph paragraph = cell.addParagraph();
            XWPFRun run = paragraph.createRun();
            run.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, imagePath, Units.toEMU(470), Units.toEMU(370));  // 이미지 크기는 필요에 따라 조절
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/downloader")
    public ResponseEntity<?> downloadFile(@RequestBody List<TB_RP720_PK> pkList) throws IOException {

        // 파일 목록과 파일 이름을 담을 리스트 초기화
        List<File> filesToDownload = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();

        // ZIP 파일 이름을 설정할 변수 초기화
        String checkdt = null;
        String checktitle = null;

        // 파일을 메모리에 쓰기
        for (TB_RP720_PK pk : pkList) {
            List<Map<String, Object>> fileList = hapReportService.download(pk);

            for (Map<String, Object> fileInfo : fileList) {
                String filePath = (String) fileInfo.get("filepath");
                String fileName = (String) fileInfo.get("filesvnm");
                String originFileName = (String) fileInfo.get("fileornm");

                if (checkdt == null) {
                    checkdt = (String) fileInfo.get("checkdt");
                }
                if (checktitle == null) {
                    checktitle = (String) fileInfo.get("checktitle");
                }

                File file = new File(filePath + File.separator + fileName);

                // 파일이 실제로 존재하는지 확인
                if (file.exists()) {
                    filesToDownload.add(file);
                    fileNames.add(originFileName); // 다운로드 받을 파일 이름을 originFileName으로 설정
                }
            }
        }

        // 파일이 없는 경우
        if (filesToDownload.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 파일이 하나인 경우 그 파일을 바로 다운로드
        if (filesToDownload.size() == 1) {
            File file = filesToDownload.get(0);
            String originFileName = fileNames.get(0); // originFileName 가져오기

            HttpHeaders headers = new HttpHeaders();
            String encodedFileName = URLEncoder.encode(originFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originFileName + "\"; filename*=UTF-8''" + encodedFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(file.length());

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }

        String zipFileName = (checkdt != null && checktitle != null) ? checkdt + "_" + checktitle + ".zip" : "download.zip";

        // 파일이 두 개 이상인 경우 ZIP 파일로 묶어서 다운로드
        ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(zipBaos)) {

            for (int i = 0; i < filesToDownload.size(); i++) {
                File file = filesToDownload.get(i);
                String originFileName = fileNames.get(i); // originFileName 가져오기

                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(originFileName);
                    zipOut.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, len);
                    }

                    zipOut.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }

            zipOut.finish();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        ByteArrayResource zipResource = new ByteArrayResource(zipBaos.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        String encodedZipFileName = URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"; filename*=UTF-8''" + encodedZipFileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(zipResource.contentLength());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipResource);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(@RequestParam String query, @RequestParam String field) {
        List<String> suggestions = hapReportService.getSuggestions(query, field);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/newform")
    public AjaxResult getByIdforNew(@RequestParam(value = "spworkcd") String spworkcd,
                                    @RequestParam(value = "spcompcd") String spcompcd,
                                    @RequestParam(value = "spplancd") String spplancd) throws IOException {
        AjaxResult result = new AjaxResult();

        Map<String, Object> item = this.hapReportService.getFirst(spworkcd, spcompcd, spplancd);
        result.data = item;
        return result;
    }
}
