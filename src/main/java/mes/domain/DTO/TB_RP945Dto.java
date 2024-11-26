package mes.domain.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class TB_RP945Dto {

    private String userid;  //사용자아이디
    private String askseq; //신청순번
    private String spworkcd; //관리대상발전시설 코드
    private String spworknm; //관리대상 발전시설명
    private String spcompcd; //관리대상 발전시설 상세코드
    private String spcompnm; //관리대상 발전시설명 상세
    private String spplancd;
    private String spplannm;
    private Integer spcompid;
    private Integer spworkid;
    private Integer spplanid;


}
