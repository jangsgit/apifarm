package mes.app.system.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.util.StringUtils;
import mes.domain.services.SqlRunner;

@Service
public class MenuLogService {

	@Autowired
	SqlRunner sqlRunner;
	
	public List<Map<String, Object>> getLogCount(String dateFrom, String dateTo, String menuCode, String userPk) {
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("dateFrom", dateFrom + " 00:00:00");
		paramMap.addValue("dateTo", dateTo + " 23:59:59");
		paramMap.addValue("menuCode", menuCode);
		paramMap.addValue("userPk", userPk);
		
        String sql = """   		
	        select mf."FolderName" as folder_name
	        , g."MenuCode" as menu_code, m."MenuName" as menu_name
	        , count(*) as use_count
	        from menu_use_log g 
	        inner join menu_item m on m."MenuCode" = g."MenuCode" 
	        left join menu_folder mf on mf.id = m."MenuFolder_id" 
	        left join auth_user u on u.id = g."User_id" 
	        where g._created between cast(:dateFrom as timestamp) and cast(:dateTo as timestamp)
	        """;
        if (StringUtils.isEmpty(menuCode)==false)  sql += "and g.\"MenuCode\" = :menuCode ";
        if (StringUtils.isEmpty(userPk)==false)  sql += "and g.\"User_id\" = cast(:userPk as Integer) ";
        
        sql += "group by mf.\"FolderName\", g.\"MenuCode\",  m.\"MenuName\"";
		
        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, paramMap);
		
		return items;
	}

	public List<Map<String, Object>> getLogList(String dateFrom, String dateTo, String menuCode, String userPk) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("dateFrom", dateFrom + " 00:00:00");
		paramMap.addValue("dateTo", dateTo + " 23:59:59");
		paramMap.addValue("menuCode", menuCode);
		paramMap.addValue("userPk", userPk);

		String sql = """
        SELECT
            row_number() OVER (ORDER BY g._created DESC) AS row_number,
            g.id,
            mf."FolderName" AS folder_name,
            g."MenuCode" AS menu_code,
            m."MenuName" AS menu_name,
            u.username,
            p."Name" AS user_name,
            to_char(g._created, 'yyyy-mm-dd hh24:mi:ss') AS click_date
        FROM
            menu_use_log g
            INNER JOIN menu_item m ON m."MenuCode" = g."MenuCode"
            LEFT JOIN menu_folder mf ON mf.id = m."MenuFolder_id"
            LEFT JOIN auth_user u ON u.id = g."User_id"
            LEFT JOIN user_profile p ON p."User_id" = g."User_id"
        WHERE
            g._created BETWEEN cast(:dateFrom AS timestamp) AND cast(:dateTo AS timestamp)
        """;

		if (StringUtils.isNotEmpty(menuCode)) {
			sql += " AND g.\"MenuCode\" = :menuCode ";
		}
		if (StringUtils.isNotEmpty(userPk)) {
			sql += " AND g.\"User_id\" = cast(:userPk AS Integer) ";
		}
		sql += " ORDER BY g._created DESC ";

		List<Map<String, Object>> items = this.sqlRunner.getRows(sql, paramMap);

		return items;
	}

	public List<Map<String, Object>> getUserList() {
		
        String sql = """   		
    			select u.id as value, u.username||'('||u.last_name||u.first_name||')' as text
	            from auth_user u 
	            inner join user_profile up on up."User_id" = u.id 
	            inner join user_group ug on ug.id = up."UserGroup_id" 
	            where u.is_active 
	            and not u.is_superuser 
	            and  ug."Code" not in ('dev')
	            order by 2
    	        """;
        
        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, null);
		
		return items;
	}

}
