package mes.app.operate.service;

import mes.domain.entity.actasEntity.TB_RP820;
import mes.domain.repository.actasRepository.TB_RP820Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_RP820Repository tb_rp820Repository;

    @Transactional
    public Boolean save(TB_RP820 tbRp820){

        try{
            tb_rp820Repository.save(tbRp820);

            return true;

        }catch (Exception e){
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

    public Map<String, Object> getRequesterInfo(String userid){

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder();
        dicParam.addValue("userid", userid);

        sql.append("""
			select au.first_name as name
              , au."tel"
              , uc."Value"
              , au.agencycd
            from auth_user au
            left join user_code uc on au.agencycd::Integer = uc.id
            where au.username = :userid
		    """);

        Map<String, Object> item = this.sqlRunner.getRow(sql.toString(), dicParam);

        return item;
    }

    public List<Map<String, Object>> getKtList(){

        MapSqlParameterSource dicParam = new MapSqlParameterSource();


        String sql = """
			select first_name as name
              , tel
            from auth_user
            where agencycd = '254'
            and is_active = 'true'
		    """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }
}
