package mes.domain.entity.actasEntity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP760Id implements Serializable{
    private String spworkcd;
    private String spcompcd;
    private String spplancd;
    private String standdt;
    private String docdv;
    private String checkseq;


}
