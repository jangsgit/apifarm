package mes.app.system;


import mes.domain.entity.DocResult;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Date;

@RestController
@RequestMapping("/api/system/Etctel_Input")
public class EtctelInputController {

    @Autowired
    SqlRunner sqlRunner;

    // 비상연락망 조회
    @GetMapping("/read")
    public AjaxResult getEtctelInput(){
        AjaxResult result = new AjaxResult();
//        result.data = items;
        return result;

    }

    // 비상연락망 상세보기
    @GetMapping("/detail")
    public AjaxResult getEtctelInputDetail(
            @RequestParam(value = "id", required = false) Integer id,
            HttpServletRequest request){

        AjaxResult result = new AjaxResult();
//        result.data = items;
        return result;

    }



    // 비상연락망 저장
//    @PostMapping("/save")
//    @Transactional
//    public AjaxResult saveEtctelInput(
//            @RequestParam(value = "id", required = false) Integer id,
//            @RequestParam(value = "keyword", required = false) String keyword,
//            @RequestParam(value = "username", required = false) String username,
//            HttpServletRequest request,
//            Authentication auth){
//
//
//        AjaxResult  result = new AjaxResult();
//
//        User user = (User) auth.getPrincipal();
//
//        Integer doc_form_id = getDocFormId(doc_form);
//
//        DocResult docResult = null;
//
//        if (doc_id == null) {
//            docResult = new DocResult();
//        } else {
//            docResult = this.docResultRepository.getDocResultById(doc_id);
//        }
//
//        docResult.setDocumentName(doc_name);
//        docResult.setDocumentFormId(doc_form_id);
//        docResult.setContent(content);
//        docResult.setDocumentDate(Date.valueOf(doc_date));
//        docResult.set_audit(user);
//
//        docResult = this.docResultRepository.save(docResult);
//
//        if (file_id != null && !file_id.isEmpty())  {
//
//            Integer data_pk = docResult.getId();
//            String[] fileIdList = file_id.split(",");
//
//            for (String fileId : fileIdList) {
//                int id = Integer.parseInt(fileId);
//                this.fileService.updateDataPk(id, data_pk);
//            }
//        }
//
//
//        return result;
//
//    }


    // 비상연락망 삭제
//    @PostMapping("/delete")
//    public AjaxResult deleteDocForm(@RequestParam("id") Integer id) {
//
//        if (id != null) {
//            this.docResultRepository.deleteById(id);
//        }
//
//        AjaxResult result = new AjaxResult();
//        return result;
//    }



}
