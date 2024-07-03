package mes.domain.entity.actasEntity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_rp760")
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP760Id.class)
public class TB_RP760 extends BaseEntity{

    @Id
    @Column(length = 3, nullable = false)
    private String spworkcd;

    @Id
    @Column(length = 3, nullable = false)
    private String spcompcd;

    @Id
    @Column(length = 3, nullable = false)
    private String spplancd;

    @Id
    @Column(length = 8, nullable = false)
    private String standdt;

    @Id
    @Column(length = 1, nullable = false)
    private String docdv;

    @Id
    @Column(length = 2, nullable = false)
    private String checkseq;


    @Column
    private String spworknm;

    @Column
    private String spcompnm;

    @Column
    private String spplannm;

    @Column
    private String filepath;

    @Column
    private String filesvnm;

    @Column
    private String fileextns;

    @Column
    private String fileurl;

    @Column
    private String fileornm;

    @Column
    private Float filesize;

    @Column
    private String filerem;

    @Column
    private String repyn;

    @Column
    private String title;

    @Column
    private String standcontent;

    @Column
    private String inusernm;



}
