package mes.domain.DTO;


import lombok.*;
import mes.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP980Dto {

    private String id;
    private String comp;
    private String per;
    private String tel;
    private String useyn;
    private Date indatem;
    private String inuserid;
    private String inusernm;
    private String mno;
    private String email;
    private String workcd;
    private String compcd;
    private String taskwork;
    private String divinm;

}
