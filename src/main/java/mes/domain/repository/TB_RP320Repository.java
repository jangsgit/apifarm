package mes.domain.repository;

import mes.domain.DTO.TB_RP320Dto;
import mes.domain.entity.actasEntity.TB_RP320;
import mes.domain.entity.actasEntity.TB_RP320_Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface TB_RP320Repository extends JpaRepository<TB_RP320, TB_RP320_Id> {
	@Query(value = "SELECT * FROM TB_RP320 t WHERE t.standdt BETWEEN :startdt AND :enddt " +
			"AND (:powerid IS NULL OR t.powerid = :powerid)", nativeQuery = true)
	List<TB_RP320> searchGeneData(@Param("startdt") String startdt, @Param("enddt") String enddt, @Param("powerid") String powerid);
	
	@Query(value = "SELECT LEFT(standdt, 7) AS yymm \n" +
			"FROM TB_RP320 \n" +
			"GROUP BY LEFT(standdt, 7) \n" +
			"ORDER BY LEFT(standdt, 7) DESC;\n", nativeQuery = true)
	List<String> findDistinctYearMonths();
	
	List<TB_RP320> findByIndatem(LocalDate indatem);
	
	
	
	// 기간별 발전량 조회
	
	// 시간별 조회
	@Query(value = "SELECT t.standdt AS period, t.powernm, " +
			"t.mevalue01 AS hour01, t.mevalue02 AS hour02, t.mevalue03 AS hour03, t.mevalue04 AS hour04, " +
			"t.mevalue05 AS hour05, t.mevalue06 AS hour06, t.mevalue07 AS hour07, t.mevalue08 AS hour08, " +
			"t.mevalue09 AS hour09, t.mevalue10 AS hour10, t.mevalue11 AS hour11, t.mevalue12 AS hour12, " +
			"t.mevalue13 AS hour13, t.mevalue14 AS hour14, t.mevalue15 AS hour15, t.mevalue16 AS hour16, " +
			"t.mevalue17 AS hour17, t.mevalue18 AS hour18, t.mevalue19 AS hour19, t.mevalue20 AS hour20, " +
			"t.mevalue21 AS hour21, t.mevalue22 AS hour22, t.mevalue23 AS hour23, t.mevalue24 AS hour24 " +
			"FROM TB_RP320 t " +
			"WHERE t.standdt = :date " +
			"AND (:powerid IS NULL OR :powerid = 'all' OR t.powernm = :powerid)", nativeQuery = true)
	List<Map<String, Object>> searchHourlyData(@Param("date") String date, @Param("powerid") String powerid);
	
	
	// 월별 조회, 분기별 조회, 반기별 조회
	@Query("SELECT new mes.domain.DTO.TB_RP320Dto(" +
			"TO_CHAR(DATE_TRUNC('month', TO_DATE(t.standdt, 'YYYY-MM-DD')), 'YYYY-MM'), " +
			"t.powerid, t.powernm, SUM(t.mevaluet)) " +
			"FROM TB_RP320 t " +
			"WHERE TO_DATE(t.standdt, 'YYYY-MM-DD') BETWEEN TO_DATE(:startdt, 'YYYY-MM-DD') AND TO_DATE(:enddt, 'YYYY-MM-DD') " +
			"AND (:powerid = 'all' OR t.powernm = :powerid) " +
			"GROUP BY DATE_TRUNC('month', TO_DATE(t.standdt, 'YYYY-MM-DD')), t.powerid, t.powernm")
	List<TB_RP320Dto> searchMonthlyData(@Param("startdt") String startdt, @Param("enddt") String enddt, @Param("powerid") String powerid);
	
	
	
	// 분기별 조회
	/*@Query("SELECT new mes.domain.DTO.TB_RP320Dto(" +
			"TO_CHAR(DATE_TRUNC('month', TO_DATE(t.standdt, 'YYYY-MM-DD')), 'YYYY-MM'), " +
			"t.powerid, t.powernm, SUM(t.mevaluet)) " +
			"FROM TB_RP320 t " +
			"WHERE TO_DATE(t.standdt, 'YYYY-MM-DD') BETWEEN TO_DATE(:startdt, 'YYYY-MM-DD') AND TO_DATE(:enddt, 'YYYY-MM-DD') " +
			"AND (:powerid = 'all' OR t.powernm = :powerid) " +
			"GROUP BY DATE_TRUNC('month', TO_DATE(t.standdt, 'YYYY-MM-DD')), t.powerid, t.powernm")
	List<TB_RP320Dto> searchQuarterlyData(@Param("startdt") String startdt, @Param("enddt") String enddt, @Param("powerid") String powerid);*/
	
	
	// 연도별 조회
	@Query("SELECT new mes.domain.DTO.TB_RP320Dto(" +
			"TO_CHAR(DATE_TRUNC('month', TO_DATE(t.standdt, 'YYYY-MM-DD')), 'YYYY-MM'), " +
			"t.powerid, t.powernm, SUM(t.mevaluet)) " +
			"FROM TB_RP320 t " +
			"WHERE TO_DATE(t.standdt, 'YYYY-MM-DD') BETWEEN TO_DATE(:startdt, 'YYYY-MM-DD') AND TO_DATE(:enddt, 'YYYY-MM-DD') " +
			"AND (:powerid = 'all' OR t.powernm = :powerid) " +
			"GROUP BY DATE_TRUNC('month', TO_DATE(t.standdt, 'YYYY-MM-DD')), t.powerid, t.powernm")
	List<TB_RP320Dto> searchYearlyData(@Param("startdt") String startdt, @Param("enddt") String enddt, @Param("powerid") String powerid);
	
	
	
	// 연도 목록 가져오기
	@Query("SELECT DISTINCT TO_CHAR(TO_DATE(t.standdt, 'YYYY-MM-DD'), 'YYYY') AS year " +
			"FROM TB_RP320 t " +
			"ORDER BY TO_CHAR(TO_DATE(t.standdt, 'YYYY-MM-DD'), 'YYYY') DESC")
	List<String> findDistinctYears();
	
	
	
	@Query("SELECT MIN(TO_DATE(standdt, 'YYYY-MM-DD')) FROM TB_RP320")
	String findMinDate();
	
	@Query("SELECT MAX(TO_DATE(standdt, 'YYYY-MM-DD')) FROM TB_RP320")
	String findMaxDate();
	
	
	
}
