package mes.app.ticket;

import mes.app.operate.service.PowerService;
import mes.app.ticket.service.TicketService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP820;
import mes.domain.entity.actasEntity.TB_RP820_PK;
import mes.domain.entity.actasEntity.TB_RP920;
import mes.domain.entity.actasEntity.TB_RP920_PK;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP820Repository;
import mes.domain.repository.TB_RP920Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/ticket/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TB_RP820Repository tb_rp820Repository;

    @Autowired
    Settings settings;

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

        items = this.ticketService.getInspecList(searchusr, startDate, endDate);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult saveTicket(@RequestParam Map<String, String> params,
                                 @RequestParam(value = "filelist", required = false) MultipartFile files,
                                 Authentication auth) throws IOException {

        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AjaxResult result = new AjaxResult();
        String newKey = "";
        String nspworkcd = params.get("spworkcd");
        String nspcompcd = params.get("spcompcd");
        String nspplancd = params.get("spplancd");

        if (nspworkcd != null && nspcompcd != null && nspplancd != null) {

            Optional<String> checknovalue = tb_rp820Repository.findMaxChecknoBySpplancd(nspworkcd, nspcompcd, nspplancd);
            if (checknovalue.isPresent()) {
                Integer checknointvalue = Integer.parseInt(checknovalue.get()) + 1;
                newKey = checknointvalue.toString();
            } else {
                newKey = "1";
            }
        }

        TB_RP820_PK pk = new TB_RP820_PK();
        pk.setSpworkcd(nspworkcd);
        pk.setSpcompcd(nspcompcd);
        pk.setSpplancd(nspplancd);
        pk.setTketnum(newKey);

        TB_RP820 tbRp820 = new TB_RP820();

        if(files != null){

            String path = settings.getProperty("file_upload_path") + "티켓";

            float fileSize = (float) files.getSize();

            if(fileSize > 52428800){
                result.message = "파일의 크기가 초과하였습니다.";
                return result;
            }

            String fileName = files.getOriginalFilename();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            String file_uuid_name = UUID.randomUUID().toString() + "." + ext;
            String saveFilePath = path;
            File saveDir = new File(saveFilePath);
            MultipartFile mFile = null;

            mFile = files;

            //디렉토리 없으면 생성
            if(!saveDir.isDirectory()){
                saveDir.mkdirs();
            }

            File saveFile = new File(path + File.separator + file_uuid_name);
            mFile.transferTo(saveFile);

            tbRp820.setFilepath(saveFilePath);
            tbRp820.setFilesvnm(file_uuid_name);
            tbRp820.setFileornm(fileName);
            tbRp820.setFilesize(fileSize);
//            tbRp820.setFileextns(params.get("fileextns"));
//            tbRp820.setFilerem();
        }

        String c_tketcrdtm = params.get("tketcrdtm").replaceAll("-","");

        tbRp820.setPk(pk);
        tbRp820.setSpworknm(params.get("spworknm"));
        tbRp820.setSpcompnm(params.get("spcompnm"));
        tbRp820.setSpplannm(params.get("spplannm"));
        tbRp820.setTketcrdtm(c_tketcrdtm);
        tbRp820.setRequester(params.get("requester"));
        tbRp820.setRequesterhp(params.get("requesterhp"));
        tbRp820.setTketnm(params.get("tketnm"));
        tbRp820.setTkettypecd("02");
        tbRp820.setTkettypenm(params.get("tkettypenm"));
        tbRp820.setTketflag(params.get("tketflag"));
        tbRp820.setTketrem(params.get("tketrem"));
        tbRp820.setTketrcpcd(params.get("tketrcpcd"));
        tbRp820.setTketrcpnm(params.get("tketrcpnm"));
        tbRp820.setTketruserid(params.get("tketruserid"));
        tbRp820.setTketrusernm(params.get("tketrusernm"));
        tbRp820.setTketactrem(params.get("tketactrem"));
        tbRp820.setRemark(params.get("remark"));
        tbRp820.setIndatem(now);
        tbRp820.setInuserid(String.valueOf(user.getId()));
        tbRp820.setInusernm(user.getUsername());

        boolean successcode = ticketService.save(tbRp820);
        if (successcode) {
            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }

        return result;
    }
}
