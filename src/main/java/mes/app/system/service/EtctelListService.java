package mes.app.system.service;

import mes.domain.repository.TB_RP980Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EtctelListService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    private TB_RP980Repository tp980Repository;

    public List<Map<String, Object>> getEtctelList(String emconper, String emconmno){
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("emconper", "%"+emconper+"%");
        params.addValue("emconmno", "%"+emconmno+"%");

        String sql= """
                select
                ROW_NUMBER() OVER (ORDER BY indatem DESC) AS rownum,
                *
                from tb_rp980 tb980
                where 1 = 1
                and "emconper" like :emconper
                and "emconmno" like :emconmno
                order by indatem desc
                """;
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql,params);
        return items;

    }

    // 새로운 emcontno 생성
    public String generateNewEmcontno() {
        Optional<Long> maxEmcontno = tp980Repository.findMaxEmcontno();
        Long newEmcontno = maxEmcontno.map(value -> value + 1).orElse(1L);
        return String.valueOf(newEmcontno);
    }


}

