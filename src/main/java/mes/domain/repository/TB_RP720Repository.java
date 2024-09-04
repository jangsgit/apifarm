package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP720;
import mes.domain.entity.actasEntity.TB_RP720_PK;
import mes.domain.entity.actasEntity.TB_RP820;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TB_RP720Repository extends JpaRepository<TB_RP720, TB_RP720_PK> {

    @Query(value = "SELECT MAX(t.checkno) FROM TB_RP720 t WHERE t.spworkcd = :spworkcd AND t.spcompcd = :spcompcd AND t.spplancd = :spplancd AND t.checkdt = :checkdt", nativeQuery = true)
    Optional<String> findMaxCheckno(@Param("spworkcd") String spworkcd,
                                    @Param("spcompcd") String spcompcd,
                                    @Param("spplancd") String spplancd,
                                    @Param("checkdt") String checkdt);

    Optional<TB_RP720> findById(TB_RP720_PK pk);

    @Query("SELECT DISTINCT t.chkaddres FROM TB_RP720 t WHERE LOWER(t.chkaddres) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findChkaddresByQuery(@Param("query") String query);

    @Query("SELECT DISTINCT t.checknm FROM TB_RP720 t WHERE LOWER(t.checknm) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findChecknmByQuery(@Param("query") String query);

}