package mes.domain.entity.actasEntity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP715Id implements Serializable {

    private String spworkcd;
    private String spcompcd;
    private String spplancd;
    private String checkseq;
    private String spuncode_id;



}
