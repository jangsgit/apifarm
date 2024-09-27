package mes.domain.entity.actasEntity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TB_RP320_Id implements Serializable {
	private String spworkcd;
	private String spcompcd;
	private String spplancd;
	
	private String standdt;
	private String powerid;
	private Integer powtime;
}
