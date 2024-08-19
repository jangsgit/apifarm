package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP750;
import mes.domain.entity.actasEntity.TB_RP750_PK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository 
public interface TB_RP750Repository extends JpaRepository<TB_RP750, TB_RP750_PK> {

    @Query(value = "SELECT MAX(t.checkseq) FROM TB_RP750 t WHERE t.spworkcd = :spworkcd AND t.spcompcd = :spcompcd AND t.spplancd = :spplancd AND t.checkdt = :checkdt", nativeQuery = true)
    Optional<String> findMaxCheckseq(@Param("spworkcd") String spworkcd,
                                     @Param("spcompcd") String spcompcd,
                                     @Param("spplancd") String spplancd,
                                     @Param("checkdt") String checkdt);

    @Query("SELECT DISTINCT t.checkarea FROM TB_RP750 t WHERE LOWER(t.checkarea) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findCheckareasByQuery(@Param("query") String query);


}