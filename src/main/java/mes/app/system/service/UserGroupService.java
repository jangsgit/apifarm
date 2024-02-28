package mes.app.system.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import mes.domain.services.SqlRunner;
@Service 
public class UserGroupService {
	
	@Autowired
	SqlRunner sqlRunner;
	
	public List<Map<String,Object>> getUserGroupList(Boolean super_user) {
		String sql = """
			select id, "Code" as code 
            , "Name" as name 
            , "Description" as description 
            , "Disabled" as disabled 
            , to_char("_created" ,'yyyy-mm-dd hh24:mi:ss') as created
            from user_group ug 
            where 1 = 1
			""";
				
			
		if (super_user == false) {
			sql += "and \"Code\" <> 'dev' ";
		}
			
		
		sql += " order by \"Name\" ";
		List<Map<String,Object>> items = this.sqlRunner.getRows(sql, null);
		return items;
		
	}
	
	public Map<String, Object> getUserGroup(int id) {
		String sql = """
			select id, "Code" as code 
            ,"Name" as name 
            ,"Description" as description 
            ,"Disabled" as disabled 
            ,to_char("_created" ,'yyyy-mm-dd hh24:mi:ss') as created
            from user_group 
            where id = :group_id
				""";
		MapSqlParameterSource dicParam = new MapSqlParameterSource();
		dicParam.addValue("group_id", id);
		
		Map<String , Object> item = this.sqlRunner.getRow(sql, dicParam);
		return item;
	}
	
	
}