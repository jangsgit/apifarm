package mes.app.definition.service;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.thymeleaf.util.MapUtils;

import mes.domain.services.CommonUtil;
import mes.domain.services.SqlRunner;

@Service
public class PriceService {

	@Autowired
	SqlRunner sqlRunner;
		
	public int saveCompanyUnitPrice(MultiValueMap<String, Object> data) {
		Integer materialId = CommonUtil.tryIntNull(data.getFirst("Material_id"));
		Integer companyId = CommonUtil.tryIntNull(data.getFirst("Company_id"));
		Timestamp applyStartDate = CommonUtil.tryTimestamp(data.getFirst("ApplyStartDate"));
		Timestamp applyEndDate = CommonUtil.tryTimestamp("2100-12-31");
		Float unitPrice = CommonUtil.tryFloatNull(data.getFirst("UnitPrice"));
		String changerName = CommonUtil.tryString(data.getFirst("ChangerName"));
		Integer userId = CommonUtil.tryIntNull(data.getFirst("user_id").toString());
		
		MapSqlParameterSource dicParam = new MapSqlParameterSource();
		dicParam.addValue("materialId", materialId);
		dicParam.addValue("companyId", companyId);
		dicParam.addValue("applyStartDate", applyStartDate, java.sql.Types.TIMESTAMP);
		dicParam.addValue("applyEndDate", applyEndDate, java.sql.Types.TIMESTAMP);
		dicParam.addValue("unitPrice", unitPrice);
		dicParam.addValue("changerName", changerName);
		dicParam.addValue("userId", userId);
		dicParam.addValue("formerUnitPrice", null);
		
		String sql = """
				select id, "UnitPrice"
	            from mat_comp_uprice
	            where "Material_id" = :materialId
	            and "Company_id" = :companyId
	            and :applyStartDate between "ApplyStartDate" and "ApplyEndDate"
				""";
		
		Map<String, Object> item = this.sqlRunner.getRow(sql, dicParam);

		if(!MapUtils.isEmpty(item)) {
			dicParam.addValue("formerUnitPrice", CommonUtil.tryFloatNull(item.get("UnitPrice")));
		}
		
		sql = """
				update mat_comp_uprice
                set "ApplyEndDate" = (:applyStartDate)::timestamp - interval '1 days'
                where "Material_id" = :materialId
                and "Company_id" = :companyId
                and :applyStartDate between "ApplyStartDate" and "ApplyEndDate"
				""";
		
		this.sqlRunner.execute(sql, dicParam);
		
		sql = """
				INSERT INTO public.mat_comp_uprice
				("_created"
				, "_creater_id"
				, "Material_id"
				, "Company_id"
				, "ApplyStartDate"
				, "ApplyEndDate" 
				, "UnitPrice"
				, "FormerUnitPrice"
				, "ChangeDate"
				, "ChangerName")
				VALUES(
				now()
				, :userId
				, :materialId 
				, :companyId
				, :applyStartDate
				, :applyEndDate
				, :unitPrice
				, :formerUnitPrice
				, now()
				, :changerName )
				""";
		return this.sqlRunner.execute(sql, dicParam);
	}
	
	public int updateCompanyUnitPrice(MultiValueMap<String, Object> data){
		Integer priceId = CommonUtil.tryIntNull(data.getFirst("price_id"));
		Timestamp applyStartDate = CommonUtil.tryTimestamp(data.getFirst("ApplyStartDate"));
		Float unitPrice = CommonUtil.tryFloatNull(data.getFirst("UnitPrice"));
		String changerName = CommonUtil.tryString(data.getFirst("ChangerName"));
		Integer userId = CommonUtil.tryIntNull(data.getFirst("user_id").toString());
		
		MapSqlParameterSource dicParam = new MapSqlParameterSource();
		dicParam.addValue("priceId", priceId);
		dicParam.addValue("applyStartDate", applyStartDate, java.sql.Types.TIMESTAMP);
		dicParam.addValue("unitPrice", unitPrice);
		dicParam.addValue("changerName", changerName);
		dicParam.addValue("userId", userId);
        
		String sql = """
				update mat_comp_uprice
                set "UnitPrice" = :unitPrice
                , "ApplyStartDate" = :applyStartDate
                , "ChangeDate" = now()
                , "ChangerName" = :changerName
                where id = :priceId
				""";
		
    	return this.sqlRunner.execute(sql, dicParam);
	}
	
public int deleteCompanyUnitPrice(int priceId){
		
		MapSqlParameterSource dicParam = new MapSqlParameterSource();        
		dicParam.addValue("priceId", priceId);
        
        String sql = """
				select id, "Material_id", "Company_id", to_char("ApplyStartDate",'yyyy-mm-dd') as "ApplyStartDate"
	            from mat_comp_uprice
	            where id = :priceId
				""";
		
		Map<String, Object> item = this.sqlRunner.getRow(sql, dicParam);
		
    	sql = " delete from mat_comp_uprice where id = :priceId";
    	this.sqlRunner.execute(sql, dicParam);
    	
    	dicParam.addValue("materialId", CommonUtil.tryIntNull(item.get("Material_id")));
    	dicParam.addValue("companyId", CommonUtil.tryIntNull(item.get("Company_id")));
    	dicParam.addValue("applyStartDate", CommonUtil.tryTimestamp(item.get("ApplyStartDate")), java.sql.Types.TIMESTAMP);
    	
    	sql = """
    			update mat_comp_uprice
	            set "ApplyEndDate" = '2100-12-31'
	            where "Material_id" = :materialId
	            and "Company_id" = :companyId
	            and "ApplyEndDate" = (:applyStartDate)::timestamp - interval '1 days'
    			""";
    	
    	return this.sqlRunner.execute(sql, dicParam);
	}
}
