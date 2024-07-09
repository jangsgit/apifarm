package mes.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "TB_RP410" )  // 도시가스청구서 정보 테이블
public class TB_RP410 {

    @Column(name="SPWORKCD")
    private String spworkcd;

    @Column(name="SPWORKNM")
    private String spworknm;

    @Column(name="SPCOMPCD")
    private String spcompcd;

    @Column(name="SPCOMPNM")
    private String spcompnm;

    @Column(name="SPPLANCD")
    private String spplancd;

    @Column(name="SPPLANNM")
    private String spplannm;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="STANDYM")
    private String standym; //기준년월 (기본키)

    @Column(name="GASUSEAMT")
    private String gasuseamt;

    @Column(name="METERMGAMT")
    private String metermgamt;

    @Column(name="IMTARRAMT")
    private String imtarramt;

    @Column(name="SAFEMGAMT")
    private String safemgamt;

    @Column(name="SUPPAMT")
    private String suppamt;

    @Column(name="TAXAMT")
    private String taxamt;

    @Column(name="TRUNAMT")
    private String trunamt;

    @Column(name="ASKAMT")
    private String askamt;

    @Column(name="USEUAMT")
    private String useuamt;

    @Column(name="SMUSEQTY")
    private String smuseqty;

    @Column(name="SMUSEHQTY")
    private String smusehqty;

    @Column(name="LMUSEQTY")
    private String lmuseqty;

    @Column(name="LMUSEHQTY")
    private String lmusehqty;

    @Column(name="LYUSEQTY")
    private String lyuseqty;

    @Column(name="LYUSEHQTY")
    private String lyusehqty;

    @Column(name="INDATEM")
    private String indatem;

    @Column(name="INUSERID")
    private String inuserid;

    @Column(name="INUSERNM")
    private String inusern;

}
