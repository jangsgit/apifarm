package mes.app.system.service;

import java.util.List;
import java.util.Map;

import mes.domain.entity.User;
import mes.domain.entity.UserGroup;
import mes.domain.repository.UserGroupRepository;
import mes.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.util.StringUtils;
import mes.domain.services.SqlRunner;

@Service
public class UserService {

    @Autowired
    SqlRunner sqlRunner;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;

    // 사용자 리스트 조회
    public List<Map<String, Object>> getUserList(boolean superUser, Integer group, String keyword, String username, Integer departId) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("group", group);
        dicParam.addValue("keyword", keyword);
        dicParam.addValue("username", username);
        dicParam.addValue("departId", departId);

        String sql = """
                select au.id
                           , up."Name"
                           , au.username as login_id
                           , up."UserGroup_id"
                           , au.email
                           , ug."Name" as group_name
                           , up."Factory_id"
                           , f."Name" as factory_name
                           , d."Name" as dept_name
                           , up."Depart_id"
                           , up.lang_code
                           , au.is_active
                           , to_char(au.date_joined ,'yyyy-mm-dd hh24:mi') as date_joined
                         from auth_user au 
                         left join user_profile up on up."User_id" = au.id
                         left join user_group ug on ug.id = up."UserGroup_id"
                         left join factory f on f.id = up."Factory_id"
                         left join depart d on d.id = up."Depart_id"
                         where is_superuser = false
                   """;

        if (superUser != true) {
            sql += "  and ug.\"Code\" <> 'dev' ";
        }

        if (group != null) {
            sql += " and ug.\"id\" = :group ";
        }

        if (StringUtils.isEmpty(keyword) == false) {
            sql += " and up.\"Name\" like concat('%%', :keyword, '%%') ";
        }

        if (StringUtils.isEmpty(username) == false) {
            sql += " and au.\"username\" = :username ";
        }
        if (departId != null) {
            sql += " and up.\"Depart_id\" = :departId ";
        }

        sql += "order by ug.\"Name\", up.\"Name\"";

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }

    // 사용자 상세정보 조회
    public Map<String, Object> getUserDetail(Integer id) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("id", id);

        String sql = """
                select au.id
                           , up."Name"
                           , au.username as login_id
                           , au.email
                           , ug."Name" as group_name
                           , up."UserGroup_id"
                           , up."Factory_id"
                           , f."Name" as factory_name
                           , d."Name" as dept_name
                           , up."Depart_id"
                           , up.lang_code
                           , au.is_active
                           , to_char(au.date_joined ,'yyyy-mm-dd hh24:mi') as date_joined
                         from auth_user au 
                         left join user_profile up on up."User_id" = au.id
                         left join user_group ug on up."UserGroup_id" = ug.id 
                         left join factory f on up."Factory_id" = f.id 
                         left join depart d on d.id = up."Depart_id"
                         where au.id = :id
                   """;

        Map<String, Object> item = this.sqlRunner.getRow(sql, dicParam);

        return item;
    }

    // 사용자 그룹 조회
    public List<Map<String, Object>> getUserGrpList(Integer id) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("id", id);

        String sql = """
                select ug.id as grp_id
                   , ug."Name" as grp_name
                   ,rd."Char1" as grp_check
                   from user_group ug 
                   left join rela_data rd on rd."DataPk2" = ug.id 
                   and "RelationName" = 'auth_user-user_group' 
                   and rd."DataPk1" = :id
                   where coalesce(ug."Code",'') <> 'dev'
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }

    public Boolean SaveUser(User user, Authentication auth, String authType, String authCode) {

        try {
            User UserEntity = userRepository.save(user);

            List<UserGroup> AuthGroup = userGroupRepository.findByCodeAndName(authType, authCode);


            MapSqlParameterSource dicParam = new MapSqlParameterSource();


            User loginUser = (User) auth.getPrincipal();


            dicParam.addValue("loginUser", loginUser.getId());

            String sql = """
                    	INSERT INTO user_profile 
                    	("_created", "_creater_id", "User_id", "lang_code", "Name", "UserGroup_id" ) 
                    	VALUES (now(), :loginUser, :User_id, :lang_code, :name, :UserGroup_id )
                    """;

            dicParam.addValue("name", user.getFirst_name());
            dicParam.addValue("lang_code", "ko-KR");
            //dicParam.addValue("UserGroup_id", );
            dicParam.addValue("User_id", UserEntity.getId());
            dicParam.addValue("lang_code", "ko-KR");
            dicParam.addValue("UserGroup_id", AuthGroup.get(0).getId());

            this.sqlRunner.execute(sql, dicParam);
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }

    }

    public List<Map<String, Object>> getUserSandanList(String id) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("id", id);

        String sql = """
                select a.username, t.spcompcd, t.spcompnm, t.spplancd, t.spplannm, t.spworkcd, t.spworknm
                	 from auth_user as a
                	join tb_rp945 t on a."username" = t."userid"
                	where a."username" = :id
                	order by t.askseq
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }

}
