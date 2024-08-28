package mes.domain.repository;

import mes.domain.entity.actasEntity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TB_RP725Repository extends JpaRepository<TB_RP725, TB_RP725_PK> {
    @Query(value = "SELECT MAX(t.Checkseq) FROM TB_RP725 t WHERE t.spworkcd = :spworkcd AND t.spcompcd = :spcompcd AND t.spplancd = :spplancd AND t.checkdt = :checkdt AND t.checkno = :checkno", nativeQuery = true)
    Optional<String> findMaxCheckseq(@Param("spworkcd") String spworkcd,
                                     @Param("spcompcd") String spcompcd,
                                     @Param("spplancd") String spplancd,
                                     @Param("checkdt") String checkdt,
                                     @Param("checkno") String checkno
    );

    List<TB_RP725> findAllByIdSpworkcdAndIdSpcompcdAndIdSpplancdAndIdCheckdtAndIdCheckno(
            String spworkcd, String spcompcd, String spplancd, String checkdt, String checkno);
}