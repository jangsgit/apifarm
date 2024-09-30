package mes.domain.entity.actasEntity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@Setter
@Table(name = "tb_rp621") //LTSA 보고서 계약보증조건 및 실적 요약
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP621Id.class)
public class TB_RP621 extends  BaseEntity{

    @Id
    @Column(name = "spworkcd", length = 3, nullable = false)
    private String spworkcd;

    @Id @Column(name = "spcompcd", length = 3, nullable = false)
    private String spcompcd;

    @Id @Column(name = "spplancd", length = 3, nullable = false)
    private String spplancd;

    @Id @Column(name = "standqy", length = 8, nullable = false)
    private String standqy;

    private String yratescond;

    private String yratesexec;

    private String yratesrerm;
    private String yeffiscond;
    private String yeffisexec;
    private String yeffisrerm;
    private String yratewcond;
    private String yratewexec;
    private String yratewrerm;
    private String yeffiwcond;
    private String yeffiwexec;
    private String yeffiwrerm;


}
