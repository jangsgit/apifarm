package mes.domain.entity.actasEntity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tb_rp622")
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP622Id.class)
public class TB_RP622 extends BaseEntity{

    @Id
    @Column(name = "spworkcd", length = 3, nullable = false)
    private String spworkcd;

    @Id @Column(name = "spcompcd", length = 3, nullable = false)
    private String spcompcd;

    @Id @Column(name = "spplancd", length = 3, nullable = false)
    private String spplancd;

    @Id @Column(name = "standqy", nullable = false)
    private String standqy;

    @Id @Column(name = "termseq", nullable = false)
    private String termseq;

    @Column(name = "standym")
    private String standym;

    @Column(name = "dayscnt")
    private Long dayscnt;

    @Column(name = "elecres")
    private Long elecres;

    @Column(name = "elecqupt")
    private Long elecqupt;

    @Column(name = "setresq")
    private Long setresq;

    @Column(name = "resuyul")
    private Long resuyul;

    @Column(name = "remark")
    private String remark;

    @Column(name = "inuserid")
    private String inuserid;

    @Column(name = "inusernm")
    private String inusernm;




}
