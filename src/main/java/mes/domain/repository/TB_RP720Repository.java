package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP720;
import mes.domain.entity.actasEntity.TB_RP820;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TB_RP720Repository extends JpaRepository<TB_RP720, Integer> {

    @Query(value = "SELECT MAX(t.checkno) FROM TB_RP720 t WHERE t.spworkcd = :spworkcd AND t.spcompcd = :spcompcd AND t.spplancd = :spplancd", nativeQuery = true)
    Optional<String> findMaxNum(@Param("spworkcd") String spworkcd,
                                @Param("spcompcd") String spcompcd,
                                @Param("spplancd") String spplancd);

}