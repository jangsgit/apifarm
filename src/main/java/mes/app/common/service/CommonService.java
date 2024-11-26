package mes.app.common.service;

import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class CommonService {

    @Autowired
    SqlRunner sqlRunner;

    @Transactional
	public List<Map<String, Object>> findByParentId(Integer Parent_id){
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("Parent_id", Parent_id);

        String sql = """
				select id, "Value" from user_code where "Parent_id" = :Parent_id order by id;
				""";
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }

    @Transactional
    public String findById(Integer id){
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("id", id);

        String sql = """
				select "Value" from user_code where id = :id;
				""";
        String item = this.sqlRunner.queryForObject(sql, dicParam, (rs, rowNum) -> rs.getString("Value"));
        return item;
    }

}
