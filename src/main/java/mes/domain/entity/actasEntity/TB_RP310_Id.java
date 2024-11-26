package mes.domain.entity.actasEntity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TB_RP310_Id implements Serializable {
	private LocalDateTime creartdt;
	private String userid;
}
