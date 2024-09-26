package mes.domain.entity.actasEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "tb_rp815")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP815Id.class)
public class TB_RP815 extends BaseEntity{

    @Id @Column(name = "spworkcd", length = 3)
    private String spworkcd;
    @Id @Column(name = "spcompcd", length = 3)
    private String spcompcd;
    @Id @Column(name = "spplancd", length = 3)
    private String spplancd;
    @Id @Column(name = "checkseq", length = 2)
    private String checkseq;

    @Id @Column(name = "spuncode_id", length = 40)
    private String spuncode_id;



    @Column(name = "filepath")
    private String filepath;

    @Column(name = "filesvnm")
    private String filesvnm;

    @Column(name = "fileextns")
    private String fileextns;

    @Column(name = "fileurl")
    private String fileurl;

    @Column(name = "fileornm")
    private String fileornm;

    @Column(name = "filesize")
    private Float filesize;

    @Column(name = "repyn")
    private String repyn;

    @Column(name = "inuserid")
    private String inuserid;

    @Column(name = "inusernm")
    private String inusernm;










}
