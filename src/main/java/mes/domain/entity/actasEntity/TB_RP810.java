package mes.domain.entity.actasEntity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Date;


@Table(name = "tb_rp810")
@Setter
@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP810_PK.class)
public class TB_RP810 extends BaseEntity implements Persistable<TB_RP810_PK> {
    @Id @Column(name = "spworkcd", length = 3)
    private String spworkcd;
    @Id @Column(name = "spcompcd", length = 3)
    private String spcompcd;
    @Id @Column(name = "spplancd", length = 3)
    private String spplancd;
    @Id @Column(name = "srnumber", length = 20)
    private String srnumber;
    @Id @Column(name = "servicertm")
    private LocalDateTime servicertm;
    @Id @Column(name = "serviceftm")
    private LocalDateTime  serviceftm;
    @Id @Column(name = "spuncode", length = 40)
    private String spuncode;


    @Column(name="\"purpvisit\"")
    String purpvisit;    // 방문목적

    @Column(name="\"sitename\"")
    String sitename;    // 사이트명

    @Column(name="\"esname\"")
    String esname;    // ES명

    @Column(name="\"sncode\"")
    String sncode;    // S-N

    @Column(name="\"servicecause\"")
    String servicecause;    // 서비스발생사유

    @Column(name="\"corrmeas\"")
    String corrmeas;    // 조치사항및세부내역

    @Column(name="\"fsresponid\"")
    String fsresponid;    // FS담당인력

    @Column(name="\"fsresponnm\"")
    String fsresponnm;    // FS담당인력명

    @Column(name="\"fieldsflag\"")
    String fieldsflag;    // 상태

    @Column(name="\"addregitem\"")
    String addregitem;    // 추가기재사항


    @Column(name="\"inuserid\"")
    String inuserid;    // 입력자id

    @Column(name="\"inusernm\"")
    String inusernm;    // 입력자명

    @Override
    public TB_RP810_PK getId() {
        return new TB_RP810_PK(spworkcd, spcompcd, spplancd, srnumber, servicertm, serviceftm, spuncode);
    }

    @Override
    public boolean isNew() {
        return super.getINDATEM() == null;
    }
}
