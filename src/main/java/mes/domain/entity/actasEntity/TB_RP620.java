package mes.domain.entity.actasEntity;


import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tb_rp620") //LTSA 보고서 개요정보 테이블
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP620Id.class)
public class TB_RP620 extends BaseEntity{

    @Id
    @Column(name = "spworkcd", length = 3, nullable = false)
    private String spworkcd;

    @Id @Column(name = "spcompcd", length = 3, nullable = false)
    private String spcompcd;

    @Id @Column(name = "spplancd", length = 3, nullable = false)
    private String spplancd;

    @Id @Column(name = "standqy", length = 8, nullable = false)
    private String standqy;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "projectnm")
    private String projectnm;

    @Column(name = "equipstat")
    private String equipstat;

    @Column(name = "reportout")
    private String reportout;

    @Column(name = "yratescond")
    private String yratescond;

    @Column(name = "yratewcond")
    private String yratewcond;

    @Column(name = "yraterem")
    private String yraterem;

    @Column(name = "yeffiscond")
    private String yeffiscond;

    @Column(name = "yeffiwcond")
    private String yeffiwcond;

    @Column(name = "yeffirem")
    private String yeffirem;

    @Column(name = "resultsdt")
    private String resultsdt;

    @Column(name = "resultedt")
    private String resultedt;

}
