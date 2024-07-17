package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_INSPEC;
import mes.domain.entity.actasEntity.TB_INSPECId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TB_INSPECRepository extends JpaRepository<TB_INSPEC, TB_INSPECId> {

    @Query("SELECT MAX(e.seq) FROM TB_INSPEC e")
    Optional<Integer> findTopByOrderBySeqDesc();
}