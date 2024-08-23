package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP320;
import mes.domain.entity.actasEntity.TB_RP320_Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
	
}
