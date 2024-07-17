package mes.domain.entity.actasEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TB_INSPECId implements Serializable{

    private String spworkcd;


    private String spcompcd;


    private String spplancd;

    private Integer seq;


}
