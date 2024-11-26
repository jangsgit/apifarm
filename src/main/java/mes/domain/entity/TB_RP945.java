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
@IdClass(TB_RP945Id.class)
public class TB_RP945 {


    @Id
    @Column(name = "\"userid\"")
    private String userid;

    @Id
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

    @Column(name = "\"spplancd\"")
    private String spplancd;

    @Column(name = "\"spplannm\"")
    private String spplannm;

    @Column(name = "\"spworkid\"")
    private Integer spworkid;

    @Column(name = "\"spcompid\"")
    private Integer spcompid;

    @Column(name = "\"spplanid\"")
    private Integer spplanid;




    public static TB_RP945 toSaveEntity(TB_RP945Dto DTO){
        TB_RP945 tbRp945Dto = new TB_RP945();
        tbRp945Dto.setUserid(DTO.getUserid());
        tbRp945Dto.setSpcompcd(DTO.getSpcompcd());
        tbRp945Dto.setAskseq(DTO.getAskseq());
        tbRp945Dto.setSpworknm(DTO.getSpworknm());
        tbRp945Dto.setSpworkcd(DTO.getSpworkcd());
        tbRp945Dto.setSpcompnm(DTO.getSpcompnm());
        tbRp945Dto.setSpplancd(DTO.getSpplancd());
        tbRp945Dto.setSpplannm(DTO.getSpplannm());
        tbRp945Dto.setSpworkid(DTO.getSpworkid());
        tbRp945Dto.setSpcompid(DTO.getSpcompid());
        tbRp945Dto.setSpplanid(DTO.getSpplanid());

        return tbRp945Dto;
    }


}
