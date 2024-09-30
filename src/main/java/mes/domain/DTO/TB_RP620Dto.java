package mes.domain.DTO;

import lombok.*;
import mes.domain.entity.actasEntity.TB_RP620;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Setter
public class TB_RP620Dto {

    private String spworkcd;
    private String spcompcd;
    private String spplancd;

    private String spworknm;
    private String spcompnm;
    private String spplannm;

    private String standqy;
    private String purpose;
    private String projectnm;
    private String equipstat;
    private String reportout;
    private String yratescond;
    private String yratewcond;
    private String yraterem;
    private String yeffiscond;
    private String yeffiwcond;
    private String yeffirem;
    private String resultsdt;
    private String resultedt;


    public TB_RP620Dto toDto(TB_RP620 entity){
        return TB_RP620Dto.builder()
                .spworkcd(entity.getSpworkcd())
                .spcompcd(entity.getSpcompcd())
                .spplancd(entity.getSpplancd())
                .spworknm(entity.getSpworknm())
                .spcompnm(entity.getSpcompnm())
                .spplannm(entity.getSpplannm())
                .standqy(entity.getStandqy())
                .purpose(entity.getPurpose())
                .projectnm(entity.getProjectnm())
                .equipstat(entity.getEquipstat())
                .reportout(entity.getReportout())
                .yratescond(entity.getYratescond())
                .yratewcond(entity.getYratewcond())
                .yraterem(entity.getYraterem())
                .yeffiscond(entity.getYeffiscond())
                .yeffiwcond(entity.getYeffiwcond())
                .yeffirem(entity.getYeffirem())
                .resultsdt(entity.getResultsdt())
                .resultedt(entity.getResultedt())
                .build();

    }

    public TB_RP620 toEntity(TB_RP620Dto dto) {
        TB_RP620 entity = new TB_RP620();

        // 기본 필드 설정
        entity.setSpworkcd(dto.getSpworkcd());
        entity.setSpcompcd(dto.getSpcompcd());
        entity.setSpplancd(dto.getSpplancd());
        entity.setSpworknm(dto.getSpworknm());
        entity.setSpplannm(dto.getSpplannm());
        entity.setSpcompnm(dto.getSpcompnm());
        entity.setStandqy(dto.getStandqy());

        entity.setPurpose(dto.getPurpose());
        entity.setProjectnm(dto.getProjectnm());
        entity.setEquipstat(dto.getEquipstat());
        entity.setReportout(dto.getReportout());

        entity.setYratescond(dto.getYratescond());
        entity.setYratewcond(dto.getYratewcond());
        entity.setYraterem(dto.getYraterem());

        entity.setYeffiscond(dto.getYeffiscond());
        entity.setYeffiwcond(dto.getYeffiwcond());
        entity.setYeffirem(dto.getYeffirem());

        entity.setResultsdt(dto.getResultsdt());
        entity.setResultedt(dto.getResultedt());

        return entity;
    }
}
