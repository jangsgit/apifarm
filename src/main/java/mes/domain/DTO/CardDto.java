package mes.domain.DTO;


import lombok.Getter;
import lombok.Setter;
import mes.domain.entity.actasEntity.CardEntity;
import org.springframework.data.relational.core.sql.In;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CardDto {

    private Integer id;
    private String title;
    private Boolean visible;
    private Integer orderNum;

    public List<CardEntity> ToCardEntity(List<CardDto> cardDtoList) {

        List<CardEntity> cardEntityList = new ArrayList<>();
        for (CardDto cardDto : cardDtoList) {
            CardEntity cardEntity = new CardEntity();
            cardEntity.setId(cardDto.getId());
            cardEntity.setTitle(cardDto.getTitle());
            cardEntity.setOrderNum(cardDto.getOrderNum());
            cardEntity.setVisible(cardDto.getVisible());
            cardEntityList.add(cardEntity);

        }

        return cardEntityList;
    }

}
