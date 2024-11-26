package mes.domain.entity.actasEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name="tb_rp880")
@Setter
@Getter
@NoArgsConstructor
public class TB_RP880 {

    @EmbeddedId
    private TB_RP880_PK id;

    @Column(name="\"spworknm\"")
    String spworknm;    // 관할지역명

    @Column(name="\"spcompnm\"")
    String spcompnm;    // 발전산단명

    @Column(name="\"spplannm\"")
    String spplannm;    // 발전소명

    @Column(name="\"contstime\"")
    Timestamp contstime;  // 제어시간from

    @Column(name="\"contetime\"")
    Timestamp contetime;   // 제어시간to

    @Column(name="\"contdrive\"")
    String contdrive;   // 이행방법

    @Column(name="\"contusr\"")
    String contusr;   // 작성자

    @Column(name="\"contarea\"")
    String contarea;   // 대상

    @Column(name="\"indatem\"")
    Date indatem;    // 입력일시

    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자id

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명
}