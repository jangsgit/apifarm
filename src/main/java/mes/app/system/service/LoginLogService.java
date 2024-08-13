package mes.app.system.service;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.util.StringUtils;
import mes.domain.services.SqlRunner;

@Service
public class LoginLogService {

    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getLoginLogList(Timestamp start, Timestamp end, String keyword) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("start", start);
        dicParam.addValue("end", end);
        dicParam.addValue("keyword", keyword);

        System.out.println("Start: " + start);
        System.out.println("End: " + end);

        String sql = """
                select row_number() over (order by ll._created desc) as row_number
                , ll.id
                , ll."Type" as type
                , ll."IPAddress" as addr
                , au.username as login_id
                , up."Name" as name
                , to_char(ll."_created" ,'yyyy-mm-dd hh24:mi:ss') as created 
                from login_log ll 
                left join auth_user au ON au.id = ll."User_id" 
                left join user_profile up on up."User_id" = ll."User_id" 
                where ll._created between :start and :end
                and (ll."Type" = 'login' or ll."Type" = 'logout')
                """;

        if (StringUtils.isNotEmpty(keyword)) {
            sql += """ 
                    and (au.username ilike concat('%%', :keyword, '%%') 
                        or up."Name" ilike concat('%%', :keyword, '%%') 
                        )
                    """;
        }

        sql += " order by ll._created desc ";

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }

}
