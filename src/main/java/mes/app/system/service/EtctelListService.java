package mes.app.system.service;

import mes.domain.DTO.TB_RP980Dto;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EtctelListService {

    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getEtctelList(String searchusr, String searchusr1){
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("searchusr", "%"+searchusr+"%");
        params.addValue("searchusr1", "%"+searchusr1+"%");

        String sql= """
                select 
                ROW_NUMBER() OVER (ORDER BY indatem DESC) AS rownum,
                *, 
                from tb_rp980 tb980
                where 1 = 1
                and "checkusr" like :paramusr 
                and "checkusr1" like :paramusr1
                order by indatem desc
                """;
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql,params);
        return items;

    }


    // 비상연락망 저장
    public List<Map<String, Object>> tb_rp980add(TB_RP980Dto dto) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("emcontno", dto.getId());
        params.addValue("emconcomp", dto.getComp());
        params.addValue("emconper", dto.getPer());
        params.addValue("emcontel", dto.getTel());
        params.addValue("useyn", dto.getUseyn());
        params.addValue("indatem", dto.getIndatem() != null ? dto.getIndatem() : LocalDateTime.now());
        params.addValue("inuserid", dto.getInuserid());
        params.addValue("inusernm", dto.getInusernm());
        params.addValue("spworkcd", dto.getWorkcd());
        params.addValue("taskwork", dto.getTaskwork());
        params.addValue("dibinm", dto.getDivinm());
        params.addValue("spcompcd", dto.getCompcd());
        params.addValue("emconemail", dto.getEmail());
        params.addValue("emconmno", dto.getMno());

        String sql = """
        INSERT INTO TB_RP980 (EMCONTNO, EMCONCOMP, EMCONPER, EMCONTEL, USEYN, INDATEM, INUSERID, INUSERNM, 
                              SPWORKCD, TASKWORK, DIVINM, SPCOMPCD, EMCONEMAIL, EMCONMNO)
        VALUES (:emcontno, :emconcomp, :emconper, :emcontel, :useyn, :indatem, :inuserid, :inusernm, 
                :spworkcd, :taskwork, :dibinm, :spcompcd, :emconemail, :emconmno)
    """;



        // 조회 쿼리 생성
        String fetchSql = """
            SELECT EMCONTNO, EMCONCOMP, EMCONPER, EMCONTEL, USEYN, INDATEM, INUSERID, INUSERNM 
            FROM TB_RP980 
            WHERE 1=1
        """;

        if (dto.getId() != null && !dto.getId().isEmpty())
            fetchSql += " AND EMCONTNO = :emcontno";

        if (dto.getComp() != null && !dto.getComp().isEmpty())
            fetchSql += " AND EMCONCOMP = :emconcomp";

        if (dto.getPer() != null && !dto.getPer().isEmpty())
            fetchSql += " AND EMCONPER = :emconper";

        if (dto.getTel() != null && !dto.getTel().isEmpty())
            fetchSql += " AND EMCONTEL = :emcontel";

        fetchSql += " ORDER BY EMCONTNO";

        // 조회 쿼리 실행
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, params);

        return items;
    }




}

