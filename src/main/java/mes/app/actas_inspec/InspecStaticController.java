package mes.app.actas_inspec;


import mes.app.UtilClass;
import mes.app.actas_inspec.service.InspecStaticService;
import mes.domain.model.AjaxResult;
import mes.domain.repository.actasRepository.TB_RP710Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inspec_statics")
public class InspecStaticController {


    @Autowired
    InspecStaticService inspecStaticService;

    @Autowired
    TB_RP710Repository tb_rp710Repository;


    @GetMapping("/RP710/read")
    public AjaxResult RP710List(@RequestParam(value = "searchfrdate", required = false) String searchfrdate,
                                @RequestParam(value = "searchtodate", required = false) String searchtodate,
                                @RequestParam(value = "searchQuarter", required = false) String searchQuarter,
                                @RequestParam(value = "searchType", required = true) String searchType
                                ){

        AjaxResult result = new AjaxResult();

        String startdate = null;
        String endDate = null;
        List<Map<String, Object>> items = new ArrayList<>();

        switch (searchType){
            //순회점검 월별 조회
            case "monthly": if(searchfrdate.contains("-")){
                startdate = searchfrdate.replaceAll("-","");
                startdate = startdate + "01";
            }else{
                startdate = searchfrdate + "01";
            }

                if(searchtodate.contains("-")){
                    endDate = searchtodate.replaceAll("-", "");
                }else{
                    endDate = searchtodate;
                }
                endDate = new UtilClass().getLastDay(endDate);

                items = inspecStaticService.getRP710List(startdate, endDate);
            break;
            //순회점검 분기별 조회
            case "quarterly":


                break;

        }


        System.out.println(startdate);
        System.out.println(endDate);


        result.success = true;
        result.data = items;
        return result;
    }

    @GetMapping("/distinctYears")
    public ResponseEntity<List<String>> getDistinctYears() {
        List<String> years = tb_rp710Repository.findDistinctYears();
        return ResponseEntity.ok(years);
    }
}
