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
    private TB_RP725_PK pk;

    @Column(name="\"spworknm\"")
    String spworknm;    // 관할지역명

    @Column(name="\"spcompnm\"")
    String spcompnm;    // 발전산단명

    @Column(name="\"spplannm\"")
    String spplannm;    // 발전소명

    @Column(name="\"checkobj\"")
    String checkobj;  // 점검대상

    @Column(name="\"checkitem\"")
    String checkitem;   // 점검항목

    @Column(name="\"indatem\"")
    Date indatem;    // 입력일시

    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자id

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명
}