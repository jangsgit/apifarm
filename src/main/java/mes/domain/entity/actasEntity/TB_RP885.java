package mes.domain.entity.actasEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.hpsf.Decimal;
import org.exolab.castor.types.DateTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name="tb_rp885")
@Setter
@Getter
@NoArgsConstructor
public class TB_RP885 {

    @EmbeddedId
    private TB_RP885_PK id;

    @Column(name="\"contseqdt\"")
    String contseqdt;    // 조치일자

    @Column(name="\"contnum\"")
    BigDecimal contnum;    // 조치량

    @Column(name="\"conttime\"")
    Timestamp conttime;    // 조치시간

    @Column(name="\"contsequsr\"")
    String contsequsr;      // 조치자

    @Column(name="\"indatem\"")
    Date indatem;    // 입력일시

    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자id

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명

}