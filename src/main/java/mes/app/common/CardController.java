package mes.app.common;


import mes.app.common.service.CardService;
import mes.domain.DTO.CardDto;
import mes.domain.entity.actasEntity.CardEntity;
import mes.domain.model.AjaxResult;
import mes.domain.repository.actasRepository.CardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/common/Card")
public class CardController {

    private final CardRepository cardRepository;
    private final CardService cardService;

    public CardController(CardRepository cardRepository, CardService cardService) {
        this.cardRepository = cardRepository;
        this.cardService = cardService;
    }

    @GetMapping("/List")
    public AjaxResult ReturnCard(){
        AjaxResult result = new AjaxResult();

        result.data = cardService.getCardList();
        result.success = true;


        return result;
    }

    @PostMapping("/Save")
    public ResponseEntity<Map<String, String>> saveSectionData(@RequestBody List<CardDto> sectionDataList){

        boolean status = cardService.insertCard(sectionDataList);

        // JSON 형식으로 응답 반환
        Map<String, String> response = new HashMap<>();

        if(status){
            response.put("status", "success");
            response.put("message", "Card data saved successfully");

        }else{
            response.put("status", "false");
            response.put("message", "Card data saved failed");
        }

        return ResponseEntity.ok(response);
    }
}
