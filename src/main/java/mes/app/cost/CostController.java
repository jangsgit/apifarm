package mes.app.cost;

import mes.app.cost.service.CostService;
import mes.domain.entity.TB_RP520;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP920;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP520Repository;
import mes.domain.repository.TB_RP920Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("api/cost/upload")
public class CostController {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    CostService costService;

    @Autowired
    TB_RP520Repository tp520Repository;

    @Autowired
    TB_RP920Repository tbRP920Repository;


//    @GetMapping("/read")
//    public AjaxResult getCostUploadList(){
//        AjaxResult ajaxResult = new AjaxResult();
//
//    }


    @GetMapping("/save")
    public AjaxResult getCostSave(
            @RequestParam(value = "spworkcd", required = false) String spworkcd,
            @RequestParam(value = "spworknm", required = false) String spworknm,
            @RequestParam(value = "spcompcd", required = false) String spcompcd,
            @RequestParam(value = "spcompnm", required = false) String spcompnm,
            @RequestParam(value = "spplancd", required = false) String spplancd,
            @RequestParam(value = "spplannm", required = false) String spplannm,
            @RequestParam(value = "expesym", required = false) String expesym,
            @RequestParam(value = "fueamt", required = false) BigDecimal fueamt,
            @RequestParam(value = "depramt", required = false) BigDecimal depramt,
            @RequestParam(value = "ltsaamt", required = false) BigDecimal ltsaamt,
            @RequestParam(value = "psqcamt", required = false) BigDecimal psqcamt,
            @RequestParam(value = "reccamt", required = false) BigDecimal reccamt,

            @RequestParam(value = "iotlamt", required = false) BigDecimal iotlamt,
            @RequestParam(value = "gigaamt", required = false) BigDecimal gigaamt,
            @RequestParam(value = "otheamt", required = false) BigDecimal otheamt,
            @RequestParam(value = "etotamt", required = false) BigDecimal etotamt,
            @RequestParam(value = "indatem", required = false) Date indatem,
            @RequestParam(value = "inuserid", required = false) String inuserid,
            @RequestParam(value = "inusernm", required = false) String inusernm,
            HttpServletRequest request,
            Authentication auth){

        AjaxResult result = new AjaxResult();

        // 유저 정보 가져오가
        User user = (User) auth.getPrincipal();
        TB_RP920 tbRP920 = new TB_RP920();


        // 현재 시간을 Timestamp로 가져오기
        Timestamp now  = new Timestamp(System.currentTimeMillis());

        // Calendar 객체를 생성하고 Timestamp 값을 설정
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());

        // Calendar 객체에서 Date 객체 가져오기
        Date date = cal.getTime();

        // TB_RP920 정보 가져오기
//        Optional<TB_RP920> optionalTB_RP920 = tbRP920Repository.findById(rp920Id);

        TB_RP520 tbRP520 = new TB_RP520();
        tbRP520.setSpworkcd(spworkcd);
        tbRP520.setSpworknm("대구");
        tbRP520.setSpcompcd(spcompcd);
        tbRP520.setSpcomnm("성서산단");
        tbRP520.setSpplancd(spplancd);
        tbRP520.setSpplannm(spplannm);
        tbRP520.setExpesym(expesym);
        tbRP520.setFueamt(fueamt);
        tbRP520.setDepramt(depramt);
        tbRP520.setLtsaamt(ltsaamt);
        tbRP520.setPsqcamt(psqcamt);
        tbRP520.setReccamt(reccamt);
        tbRP520.setIotlamt(iotlamt);
        tbRP520.setGigaamt(gigaamt);
        tbRP520.setOtheamt(otheamt);
        tbRP520.setEtotamt(etotamt);
        tbRP520.setIndatem(date);       // local 시간을 date 시간으로 변경
        tbRP520.setInuserid(String.valueOf(user.getId()));  // 입력 유저의 id
        tbRP520.setInusernm(inusernm);


        List<Map<String,Object>> items = this.costService.getCostSave(tbRP520);


        result.data = items;

        return result;
    }



}
