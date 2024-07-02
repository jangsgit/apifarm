package mes.domain.DTO;


import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime indatem;
    private String inuserid;
    private String inusernm;
    private String mno;
    private String email;
    private String workcd;
    private String compcd;
    private String taskwork;
    private String divinm;
}
