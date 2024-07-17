package mes.domain.entity.actasEntity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_inspec")
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_INSPECId.class)
public class TB_INSPEC extends BaseEntity {

    @Id
    @Column(name = "spworkcd", length = 3, nullable = false)
    private String spworkcd;

    @Id @Column(name = "spcompcd", length = 3, nullable = false)
    private String spcompcd;

    @Id @Column(name = "spplancd", length = 3, nullable = false)
    private String spplancd;

    @Id
    @Column(name = "seq")
    private Integer seq;

    @Column(name = "tabletype")
    private String tabletype;

    @Column(name = "spuncode_id")
    private String spuncode_id;

    @Column(name = "inspecnum")
    private Integer inspecnum;

    @Column(name = "inspecdivision")
    private String inspecdivision;

    @Column(name = "inspeccont")
    private String inspeccont;

    @Column(name = "inspecresult")
    private String inspecresult;


    @Column(name = "inspecreform")
    private String inspecreform;

}
