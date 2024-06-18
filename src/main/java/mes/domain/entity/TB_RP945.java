package mes.domain.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mes.domain.DTO.TB_RP945Dto;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "TB_RP945")  //TODO: 유저권한 상세테이블
public class TB_RP945 {


    @Id
    @Column(name = "\"userid\"")
    private String userid;

    @Column(name = "\"askseq\"")
    private String askseq;

    @Column(name = "\"spworkcd\"")
    private String spworkcd;

    @Column(name = "\"spworknm\"")
    private String spworknm;

    @Column(name = "\"spcompcd\"")
    private String spcompcd;

    @Column(name = "\"spcompnm\"")
    private String spcompnm;

    @OneToOne(mappedBy = "tb_rp945")  //얘는 연관관계의 주인이 아니다.
    private TB_RP940 tb_rp940;

    public static TB_RP945 toSaveEntity(TB_RP945Dto DTO){
        TB_RP945 tbRp945Dto = new TB_RP945();
        tbRp945Dto.setUserid(DTO.getUserid());
        tbRp945Dto.setSpcompcd(DTO.getSpcompcd());
        tbRp945Dto.setAskseq(DTO.getAskseq());
        tbRp945Dto.setSpworknm(DTO.getSpworknm());
        tbRp945Dto.setSpworkcd(DTO.getSpworkcd());
        tbRp945Dto.setSpcompnm(DTO.getSpcompnm());
        return tbRp945Dto;
    }


}
