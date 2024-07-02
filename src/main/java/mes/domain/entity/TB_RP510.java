package mes.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_rp510")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TB_RP510 {
	
	@Id
	@Column(name = "salesym", length = 7)
	private String salesym; // 매출년월을 기본 키로 설정
	
	@Column(name = "spworkcd", length = 3)
	private String spworkcd;
	
	@Column(name = "spcompcd", length = 3)
	private String spcompcd;
	
	@Column(name = "spplancd", length = 3)
	private String spplancd;
	
	@Column(name = "spworknm", length = 100)
	private String spworknm;
	
	@Column(name = "spcompnm", length = 100)
	private String spcompnm;
	
	@Column(name = "spplannm", length = 100)
	private String spplannm;
	
	@Column(name = "energyqty")
	private BigDecimal energyQty;
	
	@Column(name = "smpcost")
	private BigDecimal smpCost;
	
	@Column(name = "smpamt")
	private BigDecimal smpAmt;
	
	@Column(name = "reccost")
	private BigDecimal recCost;
	
	@Column(name = "recqty")
	private BigDecimal recQty;
	
	@Column(name = "recamt")
	private BigDecimal recAmt;
	
	@Column(name = "stotamt")
	private BigDecimal stotAmt;
	
	@Column(name = "indatem")
	private Timestamp inDate;
	
	@Column(name = "inuserid", length = 6)
	private String inUserId;
	
	@Column(name = "inusernm", length = 100)
	private String inUserNm;
	
}
