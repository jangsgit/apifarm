package mes.domain.entity.actasEntity;


import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mes.domain.DTO.CardDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card")

public class CardEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    @NotNull
    private String title;

    @Column(name = "visible")
    @NotNull
    private Boolean visible;

    @Column(name = "order_num")
    @NotNull
    private Integer orderNum;


    public CardDto TOCardDTO(CardEntity cardEntity){
        CardDto card = new CardDto();
        card.setId(cardEntity.getId());
        card.setTitle(cardEntity.getTitle());
        card.setVisible(cardEntity.getVisible());
        card.setOrderNum(card.getOrderNum());
        return card;
    }

    public List<CardDto> ToCardListDTO(List<CardEntity> cardEntityList){
        List<CardDto> cardDtoList = new ArrayList<>();

        for (CardEntity cardEntity : cardEntityList){
            CardDto cardDto = new CardDto();
            cardDto.setId(cardEntity.getId());
            cardDto.setTitle(cardEntity.getTitle());
            cardDto.setVisible(cardEntity.getVisible());
            cardDto.setOrderNum(cardEntity.getOrderNum());

            cardDtoList.add(cardDto);
        }
        return cardDtoList;
    }

}
