package mes.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mes.domain.DTO.TB_RP940Dto;
import mes.domain.DTO.TB_RP980Dto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_RP980") // 비상연락체계 테이블
public class TB_RP980 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMCONTNO",length = 3, nullable = false)  // 기본키
    private String id;

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

    @Column (name = "DIVINM")   // 소속부서
    private String divinm;

    @Column(name = "INDATEM")
    private Date indatem;

    @Column(name = "INUSERID")
    private String inuserid;

    @Column(name = "INUSERNM")
    private String inusernm;


    public static TB_RP980 toSaveEntity(TB_RP980Dto dto) {
        TB_RP980 entity = new TB_RP980();
        entity.setId(dto.getEmcontno());
        entity.setEmconcomp(dto.getComp());
        entity.setEmconper(dto.getPer());
        entity.setEmcontel(dto.getTel());
        entity.setUseyn(dto.getUseyn());
        entity.setIndatem(dto.getIndatem());
        entity.setInuserid(dto.getInuserid());
        entity.setInusernm(dto.getInusernm());

        entity.setDivinm(dto.getDivinm());
        entity.setEmconmno(dto.getMno());
        entity.setEmconemail(dto.getEmail());
        entity.setTaskwork(dto.getTaskwork());
        entity.setSpworkcd(dto.getWorkcd());
        entity.setSpcompcd(dto.getCompcd());
        return entity;
    }

}
