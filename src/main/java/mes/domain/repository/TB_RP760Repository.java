package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP760;
import mes.domain.entity.actasEntity.TB_RP760_PK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository 
public interface TB_RP760Repository extends JpaRepository<TB_RP760, TB_RP760_PK> {

    @Query(value = "SELECT MAX(t.fileseq) FROM TB_RP760 t WHERE t.spworkcd = :spworkcd AND t.spcompcd = :spcompcd AND t.spplancd = :spplancd AND t.checkdt = :checkdt AND t.checkseq = :checkseq", nativeQuery = true)
    Optional<String> findMaxFileseq(@Param("spworkcd") String spworkcd,
                                    @Param("spcompcd") String spcompcd,
                                    @Param("spplancd") String spplancd,
                                    @Param("checkdt") String checkdt,
                                    @Param("checkseq") String checkseq
                                    );

    List<TB_RP760> findAllByIdSpworkcdAndIdSpcompcdAndIdSpplancdAndIdCheckdtAndIdCheckseq(
            String spworkcd, String spcompcd, String spplancd, String checkdt, String checkseq);
}
