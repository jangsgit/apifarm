package mes.app.actas_inspec.service;


import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.entity.actasEntity.TB_RP760;
import mes.domain.repository.TB_RP760Repository;
import mes.domain.repository.actasRepository.TB_RP710Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class DocService {

    @Autowired
    TB_RP760Repository tb_rp760Repository;

    @Autowired
    SqlRunner sqlRunner;


    @Transactional
    public Boolean save(TB_RP760 tbRp760){

        try{
            tb_rp760Repository.save(tbRp760);

            return true;

        }catch (Exception e){
            System.out.println(e + ": 에러발생");
            return false;
        }
    }

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
