package mes.domain.DTO;

import lombok.Getter;
import lombok.Setter;
import mes.domain.entity.actasEntity.TB_RP622;

@Getter
@Setter
public class TB_RP622Dto {

    private String spworkcd;
    private String spcompcd;
    private String spplancd;
    private String spworknm;
    private String spcompnm;
    private String spplannm;


    private String standqy;
    private String termseq;

    private String standym;
    private Long dayscnt;
    private Long elecres;
    private Long elecqupt;
    private Long setresq;
    private Long resuyul;
    private String remark;

    private String inuserid;
    private String inusernm;

    public TB_RP622 toEntity() {
        TB_RP622 entity = new TB_RP622();

        entity.setSpworkcd(this.spworkcd);
        entity.setSpcompcd(this.spcompcd);
        entity.setSpplancd(this.spplancd);
        entity.setSpworknm(this.spworknm);
        entity.setSpcompnm(this.spcompnm);
        entity.setSpplannm(this.spplannm);

        entity.setStandqy(this.standqy);
        entity.setTermseq(this.termseq);

        entity.setStandym(this.standym);
        entity.setDayscnt(this.dayscnt);
        entity.setElecres(this.elecres);
        entity.setElecqupt(this.elecqupt);
        entity.setSetresq(this.setresq);
        entity.setResuyul(this.resuyul);
        entity.setRemark(this.remark);

        entity.setInuserid(this.inuserid);
        entity.setInusernm(this.inusernm);

        return entity;
    }

    public static TB_RP622Dto fromEntity(TB_RP622 entity) {
        TB_RP622Dto dto = new TB_RP622Dto();

        dto.setSpworkcd(entity.getSpworkcd());
        dto.setSpcompcd(entity.getSpcompcd());
        dto.setSpplancd(entity.getSpplancd());
        dto.setSpworknm(entity.getSpworknm());
        dto.setSpcompnm(entity.getSpcompnm());
        dto.setSpplannm(entity.getSpplannm());

        dto.setStandqy(entity.getStandqy());
        dto.setTermseq(entity.getTermseq());

        dto.setStandym(entity.getStandym());
        dto.setDayscnt(entity.getDayscnt());
        dto.setElecres(entity.getElecres());
        dto.setElecqupt(entity.getElecqupt());
        dto.setSetresq(entity.getSetresq());
        dto.setResuyul(entity.getResuyul());
        dto.setRemark(entity.getRemark());

        dto.setInuserid(entity.getInuserid());
        dto.setInusernm(entity.getInusernm());

        return dto;
    }
}