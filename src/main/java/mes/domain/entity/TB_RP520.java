package mes.domain.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "tb_rp520")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class TB_RP520 {


    @Column(name = "SPWORKCD")
        private String spworkcd;

    @Column(name = "SPWORKNM")
        private String spworknm;

    @Column(name = "SPCOMPCD")
        private String spcompcd;

    @Column(name = "SPCOMPNM")
        private String spcomnm;

    @Column(name = "SPPLANCD")
        private String spplancd;

    @Column(name = "SPPLANNM")
        private String spplannm;

    @Id
    @Column(name = "EXPESYM")
        private String expesym;

    @Column(name = "FUEAMT")
        private BigDecimal fueamt;

    @Column(name = "DEPRAMT")
        private BigDecimal depramt;

    @Column(name = "LTSAAMT")
        private BigDecimal ltsaamt;

    @Column(name = "PSQCAMT")
        private BigDecimal psqcamt;

    @Column(name = "RECCAMT")
        private BigDecimal reccamt;

    @Column(name = "IOTLAMT")
        private BigDecimal iotlamt;

    @Column(name = "GIGAAMT")
        private BigDecimal gigaamt;

    @Column(name = "OTHEAMT")
        private BigDecimal otheamt;


    @Column(name = "ETOTAMT")
        private BigDecimal etotamt;

    @Column(name="INDATEM")
        private Date indatem;

    @Column(name="INUSERID")
        private String inuserid;

    @Column(name="INUSERNM")
        private String inusernm;


}
