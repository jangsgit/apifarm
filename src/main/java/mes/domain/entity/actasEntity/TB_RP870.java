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
@Table(name="tb_rp870")
@Setter
@Getter
@NoArgsConstructor
public class TB_RP870 {

    @EmbeddedId
    private TB_RP870_PK id;

    @Column(name="\"spworknm\"")
    String spworknm;    // 관할지역명

    @Column(name="\"spcompnm\"")
    String spcompnm;    // 발전산단명

    @Column(name="\"spplannm\"")
    String spplannm;    // 발전소명

    @Column(name="\"docdv\"")
    Integer docdv;    // 문서구분

    @Column(name="\"doctitle\"")
    String doctitle;  // 제목

    @Column(name="\"docrem\"")
    String docrem;   // 문서정보

    @Column(name="\"indatem\"")
    Date indatem;   // 입력일시

    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자ID

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명
    
}