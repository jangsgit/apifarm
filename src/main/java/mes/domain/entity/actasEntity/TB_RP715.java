package mes.domain.entity.actasEntity;


import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.security.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "tb_rp715")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP715Id.class)
public class TB_RP715 extends BaseEntity{


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



    @Column(name = "spworknm")
    private String spworknm;

    @Column(name = "spcompnm")
    private String spcompnm;

    @Column(name = "spplannm")
    private String spplannm;

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

    @Column(name = "filerem")
    private String filerem;

    @Column(name = "repyn")
    private String repyn;


    @Column(name = "inuserid")
    private String inuserid;

    @Column(name = "inusernm")
    private String inusernm;


    // Getters and setters
    // (생략)
}
