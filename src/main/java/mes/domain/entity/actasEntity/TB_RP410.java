package mes.domain.entity.actasEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="TB_RP410")  // 도시가스청구서 정보 테이블
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP410Id.class)
public class TB_RP410 extends BaseEntity {

    @Id @Column(name="SPWORKCD")
    private String spworkcd;

    @Id @Column(name="SPCOMPCD")
    private String spcompcd;

    @Id @Column(name="SPPLANCD")
    private String spplancd;

    @Id @Column(name="STANDYM")
    private String standym; //기준년월 (기본키)

    @Column(name="SPPLANNM")
    private String spplannm;

    @Column(name="SPWORKNM")
    private String spworknm;

    @Column(name="SPCOMPNM")
    private String spcompnm;

    @Column(name="GASUSEAMT")
    private BigDecimal gasuseamt;

    @Column(name="METERMGAMT")
    private BigDecimal metermgamt;

    @Column(name="IMTARRAMT")
    private BigDecimal imtarramt;

    @Column(name="SAFEMGAMT")
    private BigDecimal safemgamt;

    @Column(name="SUPPAMT")
    private BigDecimal suppamt;

    @Column(name="TAXAMT")
    private BigDecimal taxamt;

    @Column(name="TRUNAMT")
    private BigDecimal trunamt;

    @Column(name="ASKAMT")
    private BigDecimal askamt;

    @Column(name="USEUAMT")
    private BigDecimal useuamt;

    @Column(name="SMUSEQTY")
    private BigDecimal smuseqty;

    @Column(name="SMUSEHQTY")
    private BigDecimal smusehqty;

    @Column(name="LMUSEQTY")
    private BigDecimal lmuseqty;

    @Column(name="LMUSEHQTY")
    private BigDecimal lmusehqty;

    @Column(name="LYUSEQTY")
    private BigDecimal lyuseqty;

    @Column(name="LYUSEHQTY")
    private BigDecimal lyusehqty;

//    @Column(name="INDATEM")
//    private Date indatem;

    @Column(name="INUSERID")
    private String inuserid;

    @Column(name="INUSERNM")
    private String inusernm;

}
