package mes.domain.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@Embeddable
@Data
public class ElecSafePk implements Serializable {

    private String spworkcd;    // 관할지역코드
    private String spcompcd;    // 발전산단코드
    private String spplancd;    // 발전소코드
    private String checksdt;    // 전기안전관리시작일
    private String checkedt;    // 전기안전관리종료일

}