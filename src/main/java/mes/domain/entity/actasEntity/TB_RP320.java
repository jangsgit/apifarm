package mes.domain.entity.actasEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_RP320") // 발전량 데이터 정보 테이블
@EntityListeners(AuditingEntityListener.class)
@IdClass(TB_RP320_Id.class)
public class TB_RP320 extends BaseEntity {
	
	@Id @Column(name = "spworkcd", length = 3, nullable = false)
	private String spworkcd;
	
	@Id @Column(name = "spcompcd", length = 3, nullable = false)
	private String spcompcd;
	
	@Id @Column(name = "spplancd", length = 3, nullable = false)
	private String spplancd;
	
	@Id
	@Column(name = "standdt", nullable = false) // 날짜
	private String standdt;
	
	@Id
	@Column(name = "powerid", nullable = false)
	private String powerid;
	
	@Column(name = "powernm")
	private String powernm;
	
	@Id
	@Column(name = "powtime", nullable = false) // 시간
	private Integer powtime;
	
	@Column(name = "smpamt") // SMP
	private Integer smpamt;
	
	@Column(name = "emamt") // 긴급정산상한가격
	private Integer emamt;
	
	@Column(name = "mevalue") // 거래량
	private Double mevalue;
	
	@Column(name = "feeamt") // 정산금
	private Integer feeamt;
	
	@Column(name = "areaamt") // 지역자원시설세
	private Integer areaamt;
	
	@Column(name = "outamt") // 배출권정산금
	private Integer outamt;
	
	@Column(name = "rpsamt") // RPS
	private Integer rpsamt;
	
	@Column(name = "difamt") // 차액정산금
	private Integer difamt;
	
	@Column(name = "sumamt") // 최종정산금
	private Integer sumamt;
	
//	@Column(name = "indatem")
//	private LocalDate indatem;
	
	@Column(name = "inuserid")
	private String inuserid;
	
	@Column(name = "inusernm")
	private String inusernm;
	
	@Column(name = "updatem", nullable = true)
	private LocalDate updatem;
	
}
