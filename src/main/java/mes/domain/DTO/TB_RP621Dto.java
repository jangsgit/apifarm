package mes.domain.DTO;

import lombok.Getter;
import lombok.Setter;
import mes.domain.entity.actasEntity.TB_RP621;

@Getter
@Setter
public class TB_RP621Dto {
    private String spworkcd;
    private String spworknm;
    private String spcompnm;
    private String spplannm;

    private String spcompcd;
    private String spplancd;
    private String standqy;

    private String yratescond;
    private String yratesexec;
    private String yratesrerm;
    private String yeffiscond;
    private String yeffisexec;
    private String yeffisrerm;
    private String yratewcond;
    private String yratewexec;
    private String yratewrerm;
    private String yeffiwcond;
    private String yeffiwexec;
    private String yeffiwrerm;

    public TB_RP621 toEntity() {
        TB_RP621 entity = new TB_RP621();
        entity.setSpworkcd(this.spworkcd);
        entity.setSpcompcd(this.spcompcd);
        entity.setSpplancd(this.spplancd);
        entity.setSpworknm(this.spworknm);
        entity.setSpcompnm(this.spcompnm);
        entity.setSpplannm(this.spplannm);
        entity.setStandqy(this.standqy);
        entity.setYratescond(this.yratescond);
        entity.setYratesexec(this.yratesexec);
        entity.setYratesrerm(this.yratesrerm);
        entity.setYeffiscond(this.yeffiscond);
        entity.setYeffisexec(this.yeffisexec);
        entity.setYeffisrerm(this.yeffisrerm);
        entity.setYratewcond(this.yratewcond);
        entity.setYratewexec(this.yratewexec);
        entity.setYratewrerm(this.yratewrerm);
        entity.setYeffiwcond(this.yeffiwcond);
        entity.setYeffiwexec(this.yeffiwexec);
        entity.setYeffiwrerm(this.yeffiwrerm);
        return entity;
    }

    public TB_RP621Dto fromEntity(TB_RP621 entity) {
        TB_RP621Dto dto = new TB_RP621Dto();
        dto.setSpworkcd(entity.getSpworkcd());
        dto.setSpcompcd(entity.getSpcompcd());
        dto.setSpplancd(entity.getSpplancd());
        dto.setSpplannm(entity.getSpplannm());
        dto.setSpworknm(entity.getSpworknm());
        dto.setSpcompnm(entity.getSpcompnm());
        dto.setStandqy(entity.getStandqy());
        dto.setYratescond(entity.getYratescond());
        dto.setYratesexec(entity.getYratesexec());
        dto.setYratesrerm(entity.getYratesrerm());
        dto.setYeffiscond(entity.getYeffiscond());
        dto.setYeffisexec(entity.getYeffisexec());
        dto.setYeffisrerm(entity.getYeffisrerm());
        dto.setYratewcond(entity.getYratewcond());
        dto.setYratewexec(entity.getYratewexec());
        dto.setYratewrerm(entity.getYratewrerm());
        dto.setYeffiwcond(entity.getYeffiwcond());
        dto.setYeffiwexec(entity.getYeffiwexec());
        dto.setYeffiwrerm(entity.getYeffiwrerm());
        return dto;
    }
}
