package mes.domain.entity.actasEntity;


import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_rp710") //순회점검 테이블
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP710Id.class)
public class TB_RP710 extends BaseEntity{


    @Id @Column(name = "spworkcd", length = 3, nullable = false)
    private String spworkcd;

    @Id @Column(name = "spcompcd", length = 3, nullable = false)
    private String spcompcd;

    @Id @Column(name = "spplancd", length = 3, nullable = false)
    private String spplancd;

    @Id @Column(name = "checkdt", length = 8, nullable = false)
    private String checkdt;

    @Id @Column(name = "checkno", length = 2, nullable = false)
    private String checkno;

    @Id @Column(name = "spuncode", length = 40, nullable = false)
    private String spuncode;


    @Column(name = "checkusr")
    private String checkusr;

    @Column(name = "checkarea")
    private String checkarea;

    @Column(name = "checkitem")
    private String checkitem;

    @Column(name = "checkplan")
    private String checkplan;

    @Column(name = "inuserid")
    private String inuserid;

    @Column(name = "inusernm")
    private String inusernm;

    @Column(name = "supplier")
    private String supplier;

    @Column(name = "checkstdt")
    private String checkstdt;

    @Column(name = "checkendt")
    private String checkendt;

    @Column(name = "flag")
    private String flag;


}
