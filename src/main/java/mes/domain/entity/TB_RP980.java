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

    @Id @Column(name = "emcontno", nullable = false)
    private String emcontno;

    @Column(name = "emconcomp") // 업체명
    private String emconcomp;

    @Column(name = "emconper")  // 담당자
    private String emconper;

    @Column(name = "emconmno")  // 모바일
    private String emconmno;

    @Column(name="emconemail")  // 이메일
    private String emconemail;

    @Column(name= "emcontel")   // 유선전화
    private String emcontel;

    @Column(name="spworkcd")    // 관할지역코드
    private String spworkcd;

    @Column(name="spcompcd")    // 발전산단코드
    private String spcompcd;

    @Column(name="taskwork")   // 담당업무
    private String taskwork;

    @Column(name = "USEYN")     // 사용여부
    private String useyn;

    @Column (name = "divinm")   // 소속부서
    private String divinm;

    @Column(name = "indatem")
    private Date indatem;

    @Column(name = "inuserid")
    private String inuserid;

    @Column(name = "inusernm")
    private String inusernm;

}
