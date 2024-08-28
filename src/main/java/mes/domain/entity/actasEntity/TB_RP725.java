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
@Table(name="tb_rp725")
@Setter
@Getter
@NoArgsConstructor
public class TB_RP725 {

    @EmbeddedId
    private TB_RP725_PK id;

    @Column(name="\"spworknm\"")
    String spworknm;    // 관할지역명

    @Column(name="\"spcompnm\"")
    String spcompnm;    // 발전산단명

    @Column(name="\"spplannm\"")
    String spplannm;    // 발전소명

    @Column(name="\"filepath\"")
    String filepath;  // 파일경로

    @Column(name="\"filesvnm\"")
    String filesvnm;   // 저장파일

    @Column(name="\"fileextns\"")
    String fileextns;   // 파일확장자

    @Column(name="\"fileurl\"")
    String fileurl;   // 파일url주소

    @Column(name="\"fileornm\"")
    String fileornm;    // 원본파일명

    @Column(name="\"filesize\"")
    float filesize;   // 파일크기

    @Column(name="\"filerem\"")
    String filerem;   // 파일내용

    @Column(name="\"repyn\"")
    String repyn;   // 대표파일여부

    @Column(name="\"indatem\"")
    Date indatem;    // 입력일시

    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자id

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명
}