package mes.domain.DTO;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP940Dto {

    private String agency; //소속사
    private String agencyDepartment; //소속부서
    private String level; //직급
    private String name; //성명
    private String tel; //사용자HP
    private String email; //사용자 mail
    private String id; //사용자id
    private String password; // 로그인 패스워드

    private String authType; //권한그룹
    private String reason; //신청사유

}


