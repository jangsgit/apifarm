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
@Table(name="tb_rp820")
@Setter
@Getter
@NoArgsConstructor
public class TB_RP820 {

    @EmbeddedId
    private TB_RP820_PK pk;

    @Column(name="\"spworknm\"")
    String spworknm;    // 관할지역명

    @Column(name="\"spcompnm\"")
    String spcompnm;    // 발전산단명

    @Column(name="\"spplannm\"")
    String spplannm;    // 발전소명

    @Column(name="\"tketcrdtm\"")
    String tketcrdtm;    // ticket발생일시

    @Column(name="\"requester\"")
    String requester;    // 요청자

    @Column(name="\"requesterhp\"")
    String requesterhp;    // 요청자 휴대폰번호

    @Column(name="\"tketnm\"")
    String tketnm;    // 티켓명

    @Column(name="\"tkettypecd\"")
    String tkettypecd;    // 유형코드

    @Column(name="\"tkettypenm\"")
    String tkettypenm;    // 유형명

    @Column(name="\"tketflag\"")
    String tketflag;    // 상태

    @Column(name="\"tketrem\"")
    String tketrem;    // 상세내용

    @Column(name="\"tketrcpcd\"")
    String tketrcpcd;    // 수신회사코드

    @Column(name="\"tketrcpnm\"")
    String tketrcpnm;    // 수신회사명

    @Column(name="\"tketruserid\"")
    String tketruserid;    // 수신자id

    @Column(name="\"tketrusernm\"")
    String tketrusernm;    // 수신자명

    @Column(name="\"tketactrem\"")
    String tketactrem;    // 조치내역

    @Column(name="\"filepath\"")
    String filepath;    // 파일경로

    @Column(name="\"filesvnm\"")
    String filesvnm;    // 저장파일

    @Column(name="\"fileextns\"")
    String fileextns;    // 파일확장자

    @Column(name="\"fileurl\"")
    String fileurl;    // 파일url주소

    @Column(name="\"fileornm\"")
    String fileornm;    // 원본파일명

    @Column(name="\"filesize\"")
    Float filesize;    // 파일크기

    @Column(name="\"filerem\"")
    String filerem;    // 파일내용

    @Column(name="\"remark\"")
    String remark;    // 비고

    @Column(name="\"indatem\"")
    Date indatem;    // 입력일시

    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자id

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명

}