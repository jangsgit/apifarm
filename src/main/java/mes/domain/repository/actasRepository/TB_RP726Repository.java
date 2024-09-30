package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_RP726;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository 
public interface TB_RP726Repository extends JpaRepository<TB_RP726, Integer> {
    List<TB_RP726> findAllBySpworkcdAndSpcompcdAndSpplancdAndCheckdtAndCheckno(
            String spworkcd, String spcompcd, String spplancd, String checkdt, String checkno);

    @Query("SELECT DISTINCT t.company FROM TB_RP726 t WHERE LOWER(t.company) LIKE LOWER(CONCAT('%', :query, '%')) AND (:table IS NULL OR t.spmenu = :table) AND t.chkflag = '0'")
    List<String> findCompany_doByQuery(@Param("query") String query, @Param("table") String table);

    @Query("SELECT DISTINCT t.jiggeub FROM TB_RP726 t WHERE LOWER(t.jiggeub) LIKE LOWER(CONCAT('%', :query, '%')) AND (:table IS NULL OR t.spmenu = :table) AND t.chkflag = '0'")
    List<String> findPosition_doByQuery(@Param("query") String query, @Param("table") String table);

    @Query("SELECT DISTINCT t.checkusr FROM TB_RP726 t WHERE LOWER(t.checkusr) LIKE LOWER(CONCAT('%', :query, '%')) AND (:table IS NULL OR t.spmenu = :table) AND t.chkflag = '0'")
    List<String> findName_doByQuery(@Param("query") String query, @Param("table") String table);

    @Query("SELECT DISTINCT t.company FROM TB_RP726 t WHERE LOWER(t.company) LIKE LOWER(CONCAT('%', :query, '%')) AND (:table IS NULL OR t.spmenu = :table) AND t.chkflag = '1'")
    List<String> findCompany_haByQuery(@Param("query") String query, @Param("table") String table);

    @Query("SELECT DISTINCT t.jiggeub FROM TB_RP726 t WHERE LOWER(t.jiggeub) LIKE LOWER(CONCAT('%', :query, '%')) AND (:table IS NULL OR t.spmenu = :table) AND t.chkflag = '1'")
    List<String> findPosition_haByQuery(@Param("query") String query, @Param("table") String table);

    @Query("SELECT DISTINCT t.checkusr FROM TB_RP726 t WHERE LOWER(t.checkusr) LIKE LOWER(CONCAT('%', :query, '%')) AND (:table IS NULL OR t.spmenu = :table) AND t.chkflag = '1'")
    List<String> findName_haByQuery(@Param("query") String query, @Param("table") String table);

}