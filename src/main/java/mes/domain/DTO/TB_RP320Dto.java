package mes.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP320Dto {
	private String period;
	private String powerid;
	private String powernm;
	private BigDecimal total;
}
