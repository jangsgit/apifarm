package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP870;
import mes.domain.entity.actasEntity.TB_RP870_PK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository 
public interface TB_RP870Repository extends JpaRepository<TB_RP870, TB_RP870_PK> {

    @Query(value = "SELECT MAX(t.checkseq) FROM TB_RP870 t WHERE t.spworkcd = :spworkcd AND t.spcompcd = :spcompcd AND t.spplancd = :spplancd AND t.registdt = :registdt", nativeQuery = true)
    Optional<String> findMaxCheckseq(@Param("spworkcd") String spworkcd,
                                     @Param("spcompcd") String spcompcd,
                                     @Param("spplancd") String spplancd,
                                     @Param("registdt") String registdt);

}