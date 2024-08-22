package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP870;
import mes.domain.entity.actasEntity.TB_RP875;
import mes.domain.entity.actasEntity.TB_RP875_PK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TB_RP875Repository extends JpaRepository<TB_RP875, TB_RP875_PK> {

    @Query(value = "SELECT MAX(t.docseq) FROM TB_RP875 t WHERE t.spworkcd = :spworkcd AND t.spcompcd = :spcompcd AND t.spplancd = :spplancd AND t.registdt = :registdt AND t.checkseq = :checkseq", nativeQuery = true)
    Optional<String> findMaxDocseq(@Param("spworkcd") String spworkcd,
                                   @Param("spcompcd") String spcompcd,
                                   @Param("spplancd") String spplancd,
                                   @Param("registdt") String registdt,
                                   @Param("checkseq") String checkseq
    );

    List<TB_RP875> findAllByIdSpworkcdAndIdSpcompcdAndIdSpplancdAndIdRegistdtAndIdCheckseq(
            String spworkcd, String spcompcd, String spplancd, String registdt, String checkseq);

    List<TB_RP875> findByFileornmAndIdSpworkcdAndIdSpcompcdAndIdSpplancdAndIdRegistdtAndIdCheckseq(
            String fileornm, String spworkcd, String spcompcd, String spplancd, String registdt, String checkseq);

}
