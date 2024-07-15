package mes.app.actas_inspec;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import mes.app.actas_inspec.service.InspecService;
import mes.app.common.service.FileService;
import mes.config.Settings;
import mes.domain.DTO.Actas_Fileset;
import mes.domain.entity.AttachFile;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.entity.actasEntity.TB_RP715;
import mes.domain.model.AjaxResult;
import mes.domain.repository.AttachFileRepository;
import mes.domain.repository.actasRepository.TB_RP710Repository;
import mes.domain.repository.actasRepository.TB_RP715Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inspec_report")
public class InspecController {

    @Autowired
    TB_RP715Repository tb_rp715Repository;

    @Autowired
    FileService attachFileService;

    @Autowired
    AttachFileRepository attachFileRepository;

    private final InspecService inspecService;
    private final Settings settings;
    private final TB_RP710Repository tb_rp710Repository;


    public InspecController(InspecService inspecService, TB_RP710Repository tb_rp710Repository, Settings settings){
        this.inspecService = inspecService;
        this.tb_rp710Repository = tb_rp710Repository;
        this.settings = settings;
    }


    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "searchusr", required = false) String searchusr,
                              @RequestParam(value = "searchfrdate", required = false) String searchfrdate,
                              @RequestParam(value = "searchtodate", required = false) String searchtodate

                              ){
        List<Map<String, Object>> items = new ArrayList<>();

        searchusr = Optional.ofNullable(searchusr).orElse("");
        searchfrdate = Optional.ofNullable(searchfrdate).orElse("20000101");
        searchtodate = Optional.ofNullable(searchtodate).orElse("29991231");

        if(searchfrdate.isEmpty()){
            searchfrdate = "20000101";
        }
        if(searchtodate.isEmpty()){
            searchtodate = "29991231";
        }


        items = this.inspecService.getInspecList(searchusr, searchfrdate, searchtodate);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    //순회점검 일지 저장
    @PostMapping("/save")
    @Transactional
    public AjaxResult saveFilter(
            //@ModelAttribute TB_RP710 tbRp710
            @RequestParam(value = "supplier", required = false) String supplier,
            @RequestParam(value = "checkstdt", required = false) String checkstdt,
            @RequestParam(value = "checkdt", required = false) String checkdt,
            @RequestParam(value = "checkendt", required = false) String checkendt,
            @RequestParam(value = "checkusr", required = false) String checkusr,
            @RequestParam(value = "checkarea", required = false) String checkarea,
            @RequestParam(value = "checkitem", required = false) String checkitem,
            @RequestParam(value = "checkplan", required = false) String checkplan,
            @RequestParam(value = "randomuuid", required = false) String randomuuid,
            @RequestParam(value = "filelist", required = false) MultipartFile[] files
            //@RequestParam Map<String, String> params
            /*@RequestParam(value = "fileNamelist", required = false) List<String> fileNamelist,
            @RequestParam(value = "fileExtList", required = false) List<String> fileExtList,
            @RequestParam(value = "fileSizeList", required = false) List<String> fileSizeList,
            @RequestParam(value = "fileSaveNmList", required = false) List<String> fileSaveNmList*/

            ){

        AjaxResult result = new AjaxResult();


        if(files != null){
            for(MultipartFile filelist : files){
                if(filelist.getSize() > 52428800){
                    result.success = false;
                    result.message = "파일사이즈가 초과하였습니다.";
                    return result;
                }
            }
        }

        /*List<Float> fileSizeList2 = new ArrayList<>();
        for (String str : fileSizeList) {

            Float number = Float.parseFloat(str.replaceAll("[\",]", ""));
            fileSizeList2.add(number);
        }*/

        TB_RP710 tbRp710dto = new TB_RP710();

//        TB_RP715 tb_rp715 = new TB_RP715();

        //System.out.println(fileSizeList2);
        String checkdtconvertvalue = checkdt.replaceAll("-","");

        String formattedValue;
        Optional<String> checknovalue = tb_rp710Repository.findMaxChecknoByCheckdt(checkdtconvertvalue);
        if(checknovalue.isPresent()){

            Integer checknointvalue = Integer.parseInt(checknovalue.get()) + 1;

            formattedValue = String.format("%02d", checknointvalue);

        }else{
            formattedValue = "01";
        }

        tbRp710dto.setSpworkcd("001");
        tbRp710dto.setSpworknm("대구");
        tbRp710dto.setSpcompcd("001");
        tbRp710dto.setSpcompnm("대구성서공단");
        tbRp710dto.setSpplancd("001");
        tbRp710dto.setSpplannm("KT대구물류센터 연료전지발전소");
        tbRp710dto.setCheckdt(checkdtconvertvalue);
        tbRp710dto.setCheckno(formattedValue);
        tbRp710dto.setCheckstdt(checkstdt);
        tbRp710dto.setCheckendt(checkendt);
        tbRp710dto.setCheckusr(checkusr);
        tbRp710dto.setCheckarea(checkarea);
        tbRp710dto.setCheckitem(checkitem);
        tbRp710dto.setCheckplan(checkplan);
        tbRp710dto.setSupplier(supplier);
        tbRp710dto.setSpuncode(randomuuid);



            String path = settings.getProperty("file_upload_path") + "순회점검일지첨부파일";

            List<TB_RP715> fileEntities = new ArrayList<>();
        /*for (int i = 0; i < fileNamelist.size(); i++) {



            TB_RP715 fileentity = new TB_RP715();
            fileentity.setSpworkcd("001");
            fileentity.setSpcompcd("001");
            fileentity.setSpplancd("001");
            fileentity.setSpuncode_id(randomuuid);
            fileentity.setSpworknm("관할지역명");
            fileentity.setSpcompnm("발전산단명");
            fileentity.setSpplannm("발전소명");
            

            fileentity.setFilepath(path);
            fileentity.setFilesvnm(fileSaveNmList.get(i).replaceAll("[,\\[\\]'\"]", ""));
            fileentity.setFileextns(fileExtList.get(i).replaceAll("[,\\[\\]'\"]", ""));
            fileentity.setFileornm(fileNamelist.get(i).replaceAll("[,\\[\\]'\"]", ""));
            fileentity.setFilesize(fileSizeList2.get(i));
            fileentity.setRepyn("N");
            fileentity.setInuserid("홍길동");
            fileentity.setInusernm("홍길동");
            fileEntities.add(fileentity);

        }*/


        boolean successcode = inspecService.save(tbRp710dto, files);
            if (successcode) {
                result.success = true;
                result.message = "저장하였습니다.";
            } else {
                result.success = false;
                result.message = "저장에 실패하였습니다.";
            }


        return result;
    }


    @PostMapping("/filesave")
    public AjaxResult fileupload(
            @RequestParam("file") MultipartFile[] file){
        AjaxResult result = new AjaxResult();



        result.success = true;


        return result;
    }

    @PostMapping("/upload")
    public Object upload(
            MultipartHttpServletRequest multiRequest,
            @RequestParam("uploadfile") MultipartFile files,
            @RequestParam(value="DataPk", required = false) Integer DataPk,
            @RequestParam(value="tableName", required = false) String tableName,
            @RequestParam(value="attachName", required = false) String attachName,
            @RequestParam(value="onlyOne", required = false) Integer onlyOne,
            @RequestParam(value="others", required = false) String others,
            @RequestParam(value="accepts", required = false) String accepts,
            @RequestParam(value="addfileext", required = false) String addfileext,
            @RequestParam(value="thumbnailYN", required = false) String thumbnailYN,
            @RequestParam(value = "randomString", required = false) String randomString,



            RedirectAttributes redirectAttributes,
            Authentication auth) {

        if (DataPk == null || DataPk < 0) {
            DataPk = 0;
        }

        User user = (User)auth.getPrincipal();
        AttachFile attachFile = null;

        TB_RP715 attacheFile = null;

        List<String> not_ext = Arrays.asList("py", "js", "aspx", "asp", "jsp", "php", "cs", "ini", "htaccess","exe","dll");
        AjaxResult result = new AjaxResult();

        Integer fileSize = (int) files.getSize();

        Float fileSize2 = (float) files.getSize();
        try {

            if (files != null) {

                if (fileSize > 52428800) { // 50m
                    result.success = false;
                    result.message = "파일사이즈가 초과하였습니다.";
                    return result;
                }
            }

            String fileName = files.getOriginalFilename();

            String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            if (StringUtils.isEmpty(accepts)==false) {
                if (accepts.contains(ext)==false) {		//accept에 ext가 들어있지 않은 경우
                    result.success = false;
                    result.message = "허용되지 않은 확장자입니다.";
                    return result;
                }
            }

            if (not_ext.contains(ext)) { 		// 지원하지 않는 파일 list에 ext가 들어있는 경우
                result.success = false;
                result.message = "허용되지 않은 확장자입니다.";
                return result;
            }

            String path = settings.getProperty("file_upload_path") + others;

            // 2021-04-06 업무룰로 인한 추가
            if (attachName == null) {
                attachName = "basic";
            }

            try {
                // 트랜젝션 필요
                // 1. 파일저장
                String file_uuid_name = UUID.randomUUID().toString() + "." + ext;
                String saveFilePath = path ;
                File saveDir = new File(saveFilePath);
                MultipartFile mFile = null;

                mFile = files;

                // 디렉토리 없으면 생성
                if (!saveDir.isDirectory()) {
                    saveDir.mkdir();
                }

                File saveFile = new File(path + File.separator + file_uuid_name);

                mFile.transferTo(saveFile);

                /*if (onlyOne !=null && onlyOne != 0 && DataPk != 0) {

                    List<AttachFile> aList = this.attachFileRepository.findByTableNameAndDataPkAndAttachNameAndFileIndex(tableName,DataPk,attachName,0);

                    if (aList.size() > 0) {
                        attachFile = aList.get(0);
                    }
                }*/

                /*if (attachFile == null) {
                    attachFile = new AttachFile();
                }*/
                if(attacheFile == null){
                    attacheFile = new TB_RP715();

                }

                // attachFile 정보저장
                /*if (attachFile.getDataPk()==null) {	//attachFile이 비어있을 경우
                    attachFile.setTableName(tableName);
                    attachFile.setDataPk(DataPk);
                    attachFile.setAttachName(attachName);
                }*/

                //attachFile.setPhysicFileName(file_uuid_name);
               // attachFile.setFileIndex(0);
                //attachFile.setFileName(fileName);
               // attachFile.setExtName(ext);
              //  attachFile.setFilePath(path);
             //   attachFile.setFileSize(fileSize);
            //    attachFile.set_audit(user);



                attacheFile.setSpworkcd("001");
                attacheFile.setSpcompcd("001");
                attacheFile.setSpplancd("001");
                attacheFile.setSpuncode_id(randomString);
                attacheFile.setSpworknm("관할지역명");
                attacheFile.setSpcompnm("발전산단명");
                attacheFile.setSpplannm("발전소명");
                attacheFile.setCheckseq("01");
                attacheFile.setFilepath(saveFilePath);
                attacheFile.setFilesvnm(file_uuid_name);
                attacheFile.setFileextns(ext);
                attacheFile.setFileornm(fileName);
                attacheFile.setFilesize(fileSize2);
                attacheFile.setRepyn("N");
                attacheFile.setInuserid("홍길동");
                attacheFile.setInusernm("홍길동");


                //attachFile = this.attachFileRepository.save(attachFile);
                attacheFile = tb_rp715Repository.save(attacheFile);

                result.data = attacheFile;

                // 2. 썸네일파일 저장 ==>구현중
                if("Y".equals(thumbnailYN)) {
                    // 추후개발
                    //thumb_path = settings.FILE_UPLOAD_PATH + others + "\\thumbnail\\"
                    String thumb_path = settings.getProperty("FILE_UPLOAD_PATH") + others +"\\thumbnail\\";

                    File thumbPath = new File(thumb_path);
                    if(!thumbPath.isDirectory()) {
                        thumbPath.mkdir();
                    }

                }

            } catch(Exception e) {
                result.success = false;
                result.message = "업로드 오류";
            }


        } catch (Exception e) {
            result.success = false;
            result.message = "업로드 오류";
        }

        HashMap<String,Object> res = new HashMap<String,Object>();
        res.put("success", true);
        res.put("fileExt", attacheFile.getFileextns());
        res.put("fileNm", attacheFile.getFileornm());
        res.put("fileSize", attacheFile.getFilesize());
        res.put("fileSaveNm", attacheFile.getFilesvnm());
        return res;
    }



    @PostMapping("/deleteFile")
    public AjaxResult deleteFile() {

        AjaxResult result = new AjaxResult();
        AttachFile af = null;

        af = new AttachFile();


        return result;

    }

    @PostMapping("/delete")
    @Transactional
    public AjaxResult delete(
            @RequestParam(value = "spuncode") String spuncode
    ){

        AjaxResult result = new AjaxResult();

        ObjectMapper mapper = new ObjectMapper();

        String cleanJson = spuncode.replaceAll("[\\[\\]\"]", "");
        String[] tokens = cleanJson.split(",");

        List<String> paramList = List.of(tokens);

        for(String param : paramList){
            System.out.println(param);
            //TODO: 이거 자식테이블먼저 삭제해야한다.
            tb_rp715Repository.deleteBySpuncodeId(param);
            tb_rp710Repository.deleteBySpuncode(param);
        }


        result.success = true;
        result.message = "성공";
        return result;
    }
}
