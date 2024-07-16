package mes.domain.entity.actasEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Table(name="tb_rp750")
@Setter
@Getter
@NoArgsConstructor
public class TB_RP750 {

    @EmbeddedId
    private TB_RP750_PK id;

    @Column(name="\"spworknm\"")
    String spworknm;    // 관할지역명

    @Column(name="\"spcompnm\"")
    String spcompnm;    // 발전산단명

    @Column(name="\"spplannm\"")
    String spplannm;    // 발전소명

    @Column(name="\"bfconscomp\"")
    String bfconscomp;  // 사전컨설팅업체

    @Column(name="\"bfconssdt\"")
    String bfconssdt;   // 사전컨설팅시작일자

    @Column(name="\"bfconsedt\"")
    String bfconsedt;   // 사전컨설팅종료일자

    @Column(name="\"bfconsres\"")
    String bfconsres;   // 사전컨설팅결과

    @Column(name="\"checkusr\"")
    String checkusr;    // 전기안전관리담당자

    @Column(name="\"endresult\"")
    String endresult;   // 최종결과

    @Column(name="\"indatem\"")
    Date indatem;   // 입력일시

    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자ID

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명

    @Column(name="\"title\"")
    String title;       // 제목

    @Column(name="\"inspecloc\"")
    String inspecloc;   // 점검장소

    @Column(name="\"inspecdt\"")
    String inspecdt;    // 점검일자

    @Column(name="\"registdt\"")
    String registdt;    // 등록일자

    @Column(name="\"docselect\"")
    String docselect;    // 등록일자

    @Column(name="\"inspecresult\"")
    String inspecresult;    // 점검결과

}