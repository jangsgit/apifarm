package mes.app.actas_inspec.service;

import mes.domain.entity.actasEntity.TB_RP750;
import mes.domain.repository.TB_RP750Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class ElecSafeService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_RP750Repository TBRP750Repository;

    @Transactional
    public Boolean save(TB_RP750 tbRp750){

        try{
            TBRP750Repository.save(tbRp750);

            return true;

        }catch (Exception e){
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    public List<Map<String, Object>> getInspecList(String searchusr, String startDate, String endDate) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        dicParam.addValue("paramusr", "%" +searchusr+ "%");

        String sql = """
                select 
                ROW_NUMBER() OVER (ORDER BY indatem DESC) AS rownum,
                *
                from tb_rp750
                order by indatem desc
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }
}
