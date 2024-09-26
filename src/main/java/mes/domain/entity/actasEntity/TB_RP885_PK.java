package mes.domain.entity.actasEntity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Data
public class TB_RP885_PK implements Serializable {

    private String checkdt;    // 작성일자
    private String contdt;     // 제어일자
    private String contseq;    // 순번

}