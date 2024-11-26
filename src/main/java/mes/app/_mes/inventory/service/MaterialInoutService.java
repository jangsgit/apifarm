package mes.app.inventory.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.util.StringUtils;
import mes.domain.services.SqlRunner;

@Service
public class MaterialInoutService {

	@Autowired
	SqlRunner sqlRunner;
	
	public List<Map<String, Object>> getMaterialInout(String srchStartDt, String srchEndDt, String housePk,
			String matType, String matGrpPk, String keyword) {
		
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("srchStartDt", srchStartDt);
		param.addValue("srchEndDt", srchEndDt);
		param.addValue("housePk", housePk);
		param.addValue("matType", matType);
		param.addValue("matGrpPk", matGrpPk);
		param.addValue("keyword", keyword);
		
		String sql = """
					select distinct mi.id as mio_pk
                    , fn_code_name('inout_type', mi."InOut") as inout
                    , mi."Material_id"
                    , mi."InputType" 
                    , mi."OutputType" 
                    , case when mi."InOut" = 'in' then fn_code_name('input_type', mi."InputType") 
	                    when mi."InOut" = 'out' then fn_code_name('output_type', mi."OutputType") end as inout_type
                    , to_char(mi."InoutDate",'yyyy-mm-dd ') as "InoutDate"
                    , to_char(mi."InoutTime", 'hh24:mi') as "InoutTime"
                    , sh."Name" as "store_house_name"
                    , m."Code" as "material_code"
                    , m."Name" as "material_name"
                    , m."CurrentStock" 
                    , m."ValidDays"
                    , m."LotSize"
                    , m."PackingUnitQty"
                    , mi."StoreHouse_id"
                    , mih2."CurrentStock" as "HouseStock"
                    , m."SafetyStock" 
                    , coalesce(mi."InputQty", 0) as "InputQty"
                    , coalesce(mi."OutputQty", 0) as "OutputQty"
                    , u2."Name" as "unit_name"
                    , mi."Description" 
                    , fn_code_name('mat_type', mg."MaterialType") as material_type
                    --, coalesce(lot_cnt.lot_count,0) as lot_count
                    , (select count(ml."LotNumber") as lot_count 
                        from mat_lot ml 
                        where ml."SourceTableName" ='mat_inout' 
                        and ml."SourceDataPk" = mi.id
                        )  as lot_count 
                    , coalesce(mi."PotentialInputQty",0) as "potentialInputQty"
                    , fn_code_name('inout_state', mi."State" ) as "inout_state"
                    , var."StateName" as "state_name"
                    , tir."JudgeCode" as judge_code
                    , m."LotUseYN" as lot_use
                    from mat_inout mi 
                    inner join material m on mi."Material_id" = m.id
                    left join mat_grp mg on mg.id = m."MaterialGroup_id"
                    inner join store_house sh on mi."StoreHouse_id" = sh.id
                    left join unit u2 on m."Unit_id" = u2.id 
                    --left join mat_order mo on mi."MaterialOrder_id" = mo.id 
                    --and m.id = mo."Material_id" 
                    left join mat_in_house mih2 on mih2."Material_id"  = m.id
                    and mih2."StoreHouse_id" = mi."StoreHouse_id"
                    left join rela_data rd on mi.id = rd."DataPk2" and rd."RelationName" = 'mat_inout_test_result' and rd."TableName2"  = 'mat_inout'
                    left join bundle_head bh on bh.id = rd."DataPk1" and rd."RelationName" = 'mat_inout_test_result' and rd."TableName1"  = 'bundle_head'
                    left join v_appr_result var on var."SourceDataPk" = bh.id and var."SourceTableName" ='bundle_head'
                    left join test_result tr on tr."SourceDataPk"  = mi.id and tr."SourceTableName" = 'mat_inout'
                    left join test_item_result tir on tr.id = tir."TestResult_id"
                    where 1 = 1
                    --and sh."HouseType" = 'material'
                    and mi."InoutDate" between cast(:srchStartDt as date) and cast(:srchEndDt as date)
				""";
		
		if (StringUtils.isEmpty(housePk)==false) sql +=" and sh.id = cast(:housePk as Integer) ";
		if (StringUtils.isEmpty(matType)==false) sql +=" and mg.\"MaterialType\" = :matType ";
		if (StringUtils.isEmpty(matGrpPk)==false) sql +=" and m.\"MaterialGroup_id\" = cast(:matGrpPk as Integer) ";
		if (StringUtils.isEmpty(keyword)==false) sql +=" and upper(m.\"Name\") like concat('%%',upper(:keyword),'%%') ";
		
		sql += " order by \"InoutDate\" desc, \"InoutTime\" desc ";
		
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, param);
        
        return items;
	}

	public List<Map<String, Object>> mioLotList(String mioId) {
		
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("mioId", mioId);
		
		String sql = """
            select 
            mi.id as mio_id
            , ml.id as ml_id
            , ml."LotNumber" 
            , m."Name" as "MaterialName"
            , m."Code" as "MaterialCode" 
            , mg."Name" as "MaterialGroupName" 
            , m."MaterialGroup_id" 
            , m."Unit_id" 
            , m."ValidDays" 
            , u."Name" as "UnitName"
            , ml."InputQty"
            , m."Thickness"
            , m."Width"
            , m."Length"
            , to_char(ml."InputDateTime",'yyyy-MM-dd hh24:mi:ss') as "InputDateTime"
            , to_char(ml."EffectiveDate",'yyyy-MM-dd') as "EffectiveDate"
            , ml."Description"
            , ml."StoreHouse_id" as store_house_id
            from mat_lot ml  
                left join material m on m.id = ml."Material_id"
                left join mat_grp mg on mg.id = m."MaterialGroup_id" 
                left join unit u on u.id = m."Unit_id" 
                left join mat_inout mi on ml."SourceDataPk" = mi.id and ml."SourceTableName" ='mat_inout'
            where mi.id = cast(:mioId as Integer) 
			""";
		
		List<Map<String, Object>> items = this.sqlRunner.getRows(sql, param);
		return items;
	}

	public List<Map<String, Object>> mioTestList(Integer mioId, Integer testResultId) {
		
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("mioId", mioId);
		param.addValue("testResultId", testResultId);
		
		String sql = """
				select ti.id, up."Name" as "CheckName", ti."ResultType" as "resultType", to_char(tir."TestDateTime", 'YYYY-MM-DD') as "testDate"
				, tir."JudgeCode", tir."CharResult" , ti."Name" as name ,tir."Char1" as result1
				, tr.id as "testResultId", tr."TestMaster_id" as "testMasterId"
				from test_item_result tir
				inner join test_result tr on tr.id = tir."TestResult_id"
				inner join test_item ti on tir."TestItem_id"  = ti.id 
				inner join user_profile up on tir."_creater_id"  = up."User_id" 
				where tr."SourceTableName" = 'mat_inout' and tr."SourceDataPk" = :mioId
				and tr.id= :testResultId
				order by ti.id
				""";
		
		
		
		List<Map<String, Object>> items = this.sqlRunner.getRows(sql, param);
		
		return items;
	}

	public List<Map<String, Object>> mioTestDefaultList() {
		
		String sql = """
				select ti.id,ti."Name" as name, ti."ResultType" as "resultType", '' as result1
				from test_item ti
				inner join test_method tm on ti."TestMethod_id"  = tm.id 
				where tm."Code"  = 'tm_001'
				order by ti.id
			    """;
		
		List<Map<String, Object>> items = this.sqlRunner.getRows(sql, null);
		
		return items;
	}

	public Map<String, Object> getEffectDate(Integer mioId) {
		
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("mioId", mioId);
		
		String sql = """
				select (case when mi."EffectiveDate" = null then null else to_char(mi."EffectiveDate", 'YYYY-MM-DD') end)  as "EffectiveDate"
				from mat_inout mi 
				inner join material m on m.id = mi."Material_id"
				where mi.id = :mioId
				""";
		
		Map<String,Object> items = this.sqlRunner.getRow(sql, param);
		
		return items;
	}

}
