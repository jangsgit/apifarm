package mes.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_RP980") // 비상연락체계 테이블
public class TB_RP980 {

    @Id
    @Column(name = "EMCONTNO", nullable = false)
    private String emcontno;

    @Column(name = "EMCONCOMP") // 업체명
    private String emconcomp;

    @Column(name = "EMCONPER")  // 담당자
    private String emconper;

    @Column(name = "EMCONMNO")  // 모바일
    private String emconmno;

    @Column(name="EMCONEMAIL")  // 이메일
    private String emconemail;

    @Column(name= "EMCONTEL")   // 유선전화
    private String emcontel;

    @Column(name="SPWORKCD")    // 관할지역코드
    private String spworkcd;

    @Column(name="SPCOMPCD")    // 발전산단코드
    private String spcompcd;

    @Column(name="TASKWORK")   // 담당업무
    private String taskwork;

    @Column(name = "USEYN")     // 사용여부
    private String useyn;

//    @Column (name = "DIVINM")   // 소속부서
//    private String divinm;

    @Column(name = "INDATEM")
    private Date indatem;

    @Column(name = "INUSERID")
    private String inuserid;

    @Column(name = "INUSERNM")
    private String inusernm;

}
