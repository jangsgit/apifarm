package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP880;
import mes.domain.entity.actasEntity.TB_RP880_PK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TB_RP880Repository extends JpaRepository<TB_RP880, TB_RP880_PK> {

    @Query(value = "SELECT MAX(t.checkno) FROM TB_RP880 t WHERE t.spworkcd = :spworkcd AND t.spcompcd = :spcompcd AND t.spplancd = :spplancd AND t.checkdt = :checkdt", nativeQuery = true)
    Optional<String> findMaxCheckno(@Param("spworkcd") String spworkcd,
                                    @Param("spcompcd") String spcompcd,
                                    @Param("spplancd") String spplancd,
                                    @Param("checkdt") String checkdt);

    @Query("SELECT DISTINCT t.contusr FROM TB_RP880 t WHERE LOWER(t.contusr) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findContusrByQuery(@Param("query") String query);

    @Query("SELECT DISTINCT t.contarea FROM TB_RP880 t WHERE LOWER(t.contarea) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findContareaByQuery(@Param("query") String query);

    @Query("SELECT DISTINCT t.contdrive FROM TB_RP880 t WHERE LOWER(t.contdrive) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findContdriveByQuery(@Param("query") String query);

}