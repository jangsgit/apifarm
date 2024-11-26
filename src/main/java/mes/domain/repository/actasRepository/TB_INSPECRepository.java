package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_INSPEC;
import mes.domain.entity.actasEntity.TB_INSPECId;
import mes.domain.entity.actasEntity.TB_RP760;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface TB_INSPECRepository extends JpaRepository<TB_INSPEC, TB_INSPECId> {

    @Query("SELECT MAX(e.seq) FROM TB_INSPEC e")
    Optional<Integer> findTopByOrderBySeqDesc();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM TB_INSPEC WHERE spuncode_id = :spuncodeId", nativeQuery = true)
    void deleteBySpuncodeId(@Param("spuncodeId") String spuncodeId);

    // seq 값으로 TB_INSPEC 엔티티 조회
    Optional<TB_INSPEC> findBySeq(Integer seq);

    List<TB_INSPEC> findAllBySpworkcdAndSpcompcdAndSpplancdAndCheckdtAndCheckno(
            String spworkcd, String spcompcd, String spplancd, String checkdt, String checkno);

}