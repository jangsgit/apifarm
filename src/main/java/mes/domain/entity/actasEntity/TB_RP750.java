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

    @Column(name="\"checktitle\"")
    String checktitle;  // 제목

    @Column(name="\"checkusr\"")
    String checkusr;   // 점검자

    @Column(name="\"checkarea\"")
    String checkarea;   // 점검장소

    @Column(name="\"bfconsres\"")
    String bfconsres;   // 점검사항

    @Column(name="\"endresult\"")
    String endresult;    // 점검결과 - 공통코드

    @Column(name="\"indatem\"")
    Date indatem;   // 입력일시

    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자ID

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명

    @Column(name="\"registdt\"")
    String registdt;    // 등록일자

    @Column(name="\"docdv\"")
    String docdv;    // 문서구분
    
}