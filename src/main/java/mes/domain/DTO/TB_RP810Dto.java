package mes.domain.DTO;

import lombok.Getter;
import lombok.Setter;
import mes.domain.entity.actasEntity.TB_RP810;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

@Getter
@Setter
public class TB_RP810Dto {

    private String spworkcd;
    private String spcompcd;
    private String spplancd;
    private String srnumber;
    private LocalDateTime servicertm;
    private LocalDateTime serviceftm;
    private String spuncode;
    private String spworknm;
    private String spcompnm;
    private String spplannm;
    private String purpvisit;
    private String sitename;
    private String esname;
    private String sncode;
    private String servicecause;
    private String corrmeas;
    private String fsresponid;
    private String fsresponnm;
    private String fieldsflag;
    private String addregitem;
    private String inuserid;
    private String inusernm;

    public TB_RP810 toEntity(TB_RP810Dto dto){
        TB_RP810 entity = new TB_RP810();

        entity.setSpworkcd(dto.getSpworkcd());
        entity.setSpcompcd(dto.getSpcompcd());  // 주의: DTO에는 spcompcd이고, Entity에는 spcompdcd로 명시되어 있음
        entity.setSpplancd(dto.getSpplancd());
        entity.setSrnumber(dto.getSrnumber());
        entity.setServicertm(dto.getServicertm());
        entity.setServiceftm(dto.getServiceftm());
        entity.setSpuncode(dto.getSpuncode());
        entity.setSpworknm(dto.getSpworknm());
        entity.setSpcompnm(dto.getSpcompnm());
        entity.setSpplannm(dto.getSpplannm());
        entity.setPurpvisit(dto.getPurpvisit());
        entity.setSitename(dto.getSitename());
        entity.setEsname(dto.getEsname());
        entity.setSncode(dto.getSncode());
        entity.setServicecause(dto.getServicecause());
        entity.setCorrmeas(dto.getCorrmeas());
        entity.setFsresponid(dto.getFsresponid());
        entity.setFsresponnm(dto.getFsresponnm());
        entity.setFieldsflag(dto.getFieldsflag());
        entity.setAddregitem(dto.getAddregitem());
        entity.setInuserid(dto.getInuserid());
        entity.setInusernm(dto.getInusernm());

        return entity;
    }

}
