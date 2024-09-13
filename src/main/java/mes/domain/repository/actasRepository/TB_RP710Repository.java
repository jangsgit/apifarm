package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.entity.actasEntity.TB_RP710Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TB_RP710Repository extends JpaRepository<TB_RP710, TB_RP710Id> {


    // 연도 목록 가져오기
    @Query("SELECT DISTINCT TO_CHAR(TO_DATE(t.checkdt, 'YYYYMMDD'), 'YYYY') AS year " +
            "from TB_RP710 t " +
            "ORDER BY TO_CHAR(TO_DATE(t.checkdt, 'YYYYMMDD'), 'YYYY') DESC")
    List<String> findDistinctYears();

    @Query(value = "SELECT t.checkno FROM TB_RP710 t WHERE t.checkdt = :checkdtconvertvalue ORDER BY t.checkno DESC limit 1", nativeQuery = true)
    Optional<String> findMaxChecknoByCheckdt(@Param("checkdtconvertvalue") String checkdtconvertvalue);

    void deleteBySpuncode(String spuncode);

    @Modifying
    @Query("UPDATE TB_RP710 t SET t.flag = 'Y' WHERE t.spuncode = :spuncode")
    void updateFlagToYBySpuncode(@Param("spuncode") String spuncode);

    @Query("SELECT DISTINCT t.checkarea FROM TB_RP710 t WHERE LOWER(t.checkarea) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findCheckareasByQuery(@Param("query") String query);

    @Query("SELECT DISTINCT t.supplier FROM TB_RP710 t WHERE LOWER(t.supplier) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findSuppliersByQuery(@Param("query") String query);

    @Query("SELECT DISTINCT t.checkusr FROM TB_RP710 t WHERE LOWER(t.checkusr) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findCheckusrsByQuery(@Param("query") String query);

    @Query("SELECT t from TB_RP710 t ORDER BY t.INDATEM DESC")
    List<TB_RP710> findAllDesc();

    TB_RP710 findTopByOrderByINDATEMDesc();
}