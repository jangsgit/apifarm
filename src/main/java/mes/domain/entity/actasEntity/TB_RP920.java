package mes.domain.entity.actasEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="tb_rp920")
@Setter
@Getter
@NoArgsConstructor
public class TB_RP920 {

    @EmbeddedId
    private TB_RP920_PK pk;

    @Column(name="\"spworknm\"")
    String spworknm;    // 관할지역명

    @Column(name="\"spcompnm\"")
    String spcompnm;    // 발전산단명

    @Column(name="\"spplannm\"")
    String spplannm;    // 발전소명

    @Column(name="\"spwtycd\"")
    String spwtycd;    // 발전유형코드

    @Column(name="\"spwtynm\"")
    String spwtynm;    // 발전유형명

    @Column(name="\"makercd\"")
    String makercd;    // 제조사코드

    @Column(name="\"makernm\"")
    String makernm;    // 제조사명

    @Column(name="\"setupdt\"")
    String setupdt;    // 설립년월일

    @Column(name="\"pwcapa\"")
    Double pwcapa;    // 발전용량

    @Column(name="\"postno\"")
    String postno;    // 우편번호

    @Column(name="\"address1\"")
    String address1;    // 기본주소

    @Column(name="\"address2\"")
    String address2;    // 상세주소

    @Column(name="\"workyn\"")
    String workyn;    // 가동유무

    @Column(name="\"mcltcd\"")
    String mcltcd;    // 유지보수사코드

    @Column(name="\"mcltnm\"")
    String mcltnm;    // 유지보수사명

    @Column(name="\"mcltusrnm\"")
    String mcltusrnm;    // 관리자

    @Column(name="\"mcltusrhp\"")
    String mcltusrhp;    // 관리자연락처

    @Column(name="\"remark\"")
    String remark;    // 비고

    @Column(name="\"filepath\"")
    String filepath;    // 파일경로

    @Column(name="\"filesvnm\"")
    String filesvnm;    // 저장파일

    @Column(name="\"fileurl\"")
    String fileurl;    // 파일url주소

    @Column(name="\"fileornm\"")
    String fileornm;    // 원본파일명

    @Column(name="\"filesize\"")
    Float filesize;    // 파일크기

    @Column(name="\"filerem\"")
    String filerem;    // 파일내용

    @Column(name="\"indatem\"")
    Date indatem;    // 입력일시

    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자id

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명

}