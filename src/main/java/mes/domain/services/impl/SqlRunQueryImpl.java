package mes.domain.services.impl;


import java.util.List;
import java.util.Map;

import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import mes.domain.services.LogWriter;
import mes.domain.services.SqlRunner;


@Repository
public class SqlRunQueryImpl implements SqlRunner {

	@Autowired(required = true)
    private NamedParameterJdbcTemplate  jdbcTemplate;
	
	@Autowired
	LogWriter logWriter;


	public List<Map<String, Object>> selectList(String sql, Map<String, Object> dicParam){

		List<Map<String, Object>> rows = null;
		try {
			// SQL 실행
			rows = jdbcTemplate.queryForList(sql, dicParam);
		} catch (DataAccessException dae) {
			// 데이터 액세스 관련 예외 처리
			System.err.println("DataAccessException 발생: " + dae.getMessage());
			logWriter.addDbLog("error", "SqlRunQueryImpl.selectList", dae);
		} catch (Exception e) {
			// 일반 예외 처리
			System.err.println("예기치 못한 오류 발생: " + e.getMessage());
			logWriter.addDbLog("error", "SqlRunQueryImpl.selectList", e);
		}

		// 결과 반환
		return rows != null ? rows : List.of(); // null 방지
	}


	public List<Map<String, Object>> getRows(String sql, MapSqlParameterSource dicParam){
    	
    	List<Map<String, Object>> rows = null;
    	
    	
    	try {
    		rows = this.jdbcTemplate.queryForList(sql, dicParam);
		} 
    	catch(DataAccessException de) {
    		System.out.println(de);
    	}
    	catch (Exception e) {
			// TODO: handle exception
			logWriter.addDbLog("error", "SqlRunQueryImpl.getRows", e);
		}
    	return rows;
    }
    
    public Map<String, Object> getRow(String sql, MapSqlParameterSource dicParam){    	

    	Map<String, Object> row = null;
    	
    	try {
    		row = this.jdbcTemplate.queryForMap(sql, dicParam);
		} 
    	catch(DataAccessException de) {
    		
    	
    	}
    	catch (Exception e) {
			// TODO: handle exception
			logWriter.addDbLog("error", "SqlRunQueryImpl.getRow", e);
		}
    	return row;
    }
    
    public int execute(String sql, MapSqlParameterSource dicParam) {
    	
    	int rowEffected = 0;
    	// TODO Auto-generated method stub
    	try {
    		rowEffected = this.jdbcTemplate.update(sql, dicParam);
		} catch (Exception e) {
			// TODO: handle exception
			logWriter.addDbLog("error", "SqlRunQueryImpl.excute", e);
		}
    	
    	return rowEffected;
    }
    
    public int queryForCount(String sql,  MapSqlParameterSource dicParam) {
    	//select count(*) from xxx where ~
    	return this.jdbcTemplate.queryForObject(sql, dicParam, int.class);
    }
    
    public <T> T queryForObject(String sql,  MapSqlParameterSource dicParam, RowMapper<T> mapper) throws DataException {
    	T rr= this.jdbcTemplate.queryForObject(sql, dicParam, mapper); 
    	return rr;    	
    }
    
}
