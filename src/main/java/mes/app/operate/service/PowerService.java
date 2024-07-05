package mes.app.operate.service;

import mes.domain.entity.actasEntity.TB_RP760;
import mes.domain.entity.actasEntity.TB_RP920;
import mes.domain.repository.PowerRepository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class PowerService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    PowerRepository powerRepository;

    @Transactional
    public Boolean save(TB_RP920 tbRp920){

        try{
            powerRepository.save(tbRp920);

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
                from tb_rp920
                order by indatem desc
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }
}
