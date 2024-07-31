package mes.app.actas_inspec.service;



import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class DocService {


    @Autowired
    SqlRunner sqlRunner;


//    @Transactional
//    public Boolean save(TB_RP770 tbRp760){
//
//        try{
//            tb_rp770Repository.save(tbRp76
//            0);
//
//            return true;
//
//        }catch (Exception e){
//            System.out.println(e + ": 에러발생");
//            return false;
//        }
//    }

    public List<Map<String, Object>> getInspecList(String searchusr) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();


        dicParam.addValue("paramusr", "%" +searchusr+ "%");

        String sql = """
                select 
                ROW_NUMBER() OVER (ORDER BY indatem DESC) AS rownum,
                *,
                 CASE
                                                       WHEN docdv = '1' THEN '서식1'
                                                       WHEN docdv = '2' THEN '서식1'
                                                       ELSE NULL
                                                   END AS doctype  
                from tb_rp760 sb
                where 1 = 1
               and "title" like :paramusr
                order by indatem desc
                """;
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }
}
