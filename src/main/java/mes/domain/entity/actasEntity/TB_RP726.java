package mes.domain.entity.actasEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="tb_rp726")
@Setter
@Getter
@NoArgsConstructor
public class TB_RP726 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="\"seq\"")
    Integer seq;        // 순번

    @Column(name="\"spworkcd\"")
    String spworkcd;    // 관할지역코드

    @Column(name="\"spcompcd\"")
    String spcompcd;    // 발전산단코드

    @Column(name="\"spplancd\"")
    String spplancd;    // 발전소코드

    @Column(name="\"spmenu\"")
    String spmenu;      // 메뉴명

    @Column(name="\"checkdt\"")
    String checkdt;     // 점검일자

    @Column(name="\"checkno\"")
    String checkno;     // 점검번호

    @Column(name="\"chkflag\"")
    String chkflag;     // 구분(0:도급, 1:수급)

    @Column(name="\"company\"")
    String company;     // 업체명

    @Column(name="\"jiggeub\"")
    String jiggeub;     // 직급

    @Column(name="\"checkusr\"")
    String checkusr;    // 성명

}