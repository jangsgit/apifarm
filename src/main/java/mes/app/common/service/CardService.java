package mes.app.common.service;


import mes.domain.DTO.CardDto;
import mes.domain.entity.actasEntity.CardEntity;
import mes.domain.repository.actasRepository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<CardDto> getCardList(){
        List<CardEntity> cardEntityList = cardRepository.findAllByOrderByOrderNumAsc();

        return new CardEntity().ToCardListDTO(cardEntityList);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean insertCard(List<CardDto> cardDto){

        try {
            List<CardEntity> cardEntityList = new CardDto().ToCardEntity(cardDto);
            cardRepository.saveAll(cardEntityList);

            return true;
        }catch (Exception e){
            // 예외가 발생할 경우 예외 메시지 출력 및 false 반환
            System.err.println("Error occurred while inserting cards: " + e.getMessage());

            // 실패 시 false 반환
            return false;
        }
    }
}
