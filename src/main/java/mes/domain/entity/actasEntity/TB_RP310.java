package mes.domain.entity.actasEntity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "TB_RP310")
@IdClass(TB_RP310_Id.class)
public class TB_RP310 {
	
	@Column(name = "spworkcd")
	private String spworkcd;
	
	@Column(name = "spworknm")
	private String spworknm;
	
	@Column(name = "spcompcd")
	private String spcompcd;
	
	@Column(name = "spcompnm")
	private String spcompnm;
	
	@Column(name = "spplancd")
	private String spplancd;
	
	@Column(name = "spplannm")
	private String spplannm;
	
	@Column(name = "standdt")
	private String standdt;
	
	@Id
	@Column(name = "userid")
	private String userid;
	
	@Column(name = "usernm")
	private String usernm;
	
	@Id
	@Column(name = "creartdt")
	private LocalDateTime creartdt;
	
	@Column(name = "powerid", nullable = true)
	private String powerid;
	
	@Column(name = "powernm", nullable = true)
	private String powernm;
	
	@Column(name = "uptyn")
	private String uptyn; // 업데이트 여부 필드 추가, 'Y'로 설정

}
