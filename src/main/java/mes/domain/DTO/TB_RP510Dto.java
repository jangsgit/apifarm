package mes.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TB_RP510Dto {
	
	//	private Long id;
	private String salesym; // 매출년월을 기본 키로 사용
	private String spworkcd; // 관할지역코드
	private String spworknm; // 관할지역명
	private String spcompcd; // 발전산단코드
	private String spcompnm; // 발전산단명
	private String spplancd; // 발전소코드
	private String spplannm; // 발전소명
	
	private BigDecimal energyQty; // 발전량
	private BigDecimal smpCost; // SMP 단가
	private BigDecimal smpAmt; // SMP 매출
	private BigDecimal recCost; // REC 단가
	private BigDecimal recQty; // REC 수량
	private BigDecimal recAmt; // REC 매출
	private BigDecimal stotAmt; // 총매출 (총발전매출과 같은 의미?)
	private Timestamp inDate;     // 입력일시
	private String inUserId;      // 입력자ID
	private String inUserNm;      // 입력자명
	
	private BigDecimal recAverageCost;  // REC 평균단가(외부에서 제공받거나 계산)
	
}
