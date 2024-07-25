package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_RP715;
import mes.domain.entity.actasEntity.TB_RP715Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;


@Repository
public interface TB_RP715Repository extends JpaRepository<TB_RP715, TB_RP715Id> {

    @Query(value = "SELECT t.checkseq FROM TB_RP715 t WHERE t.spuncode_id = :spuncode_id ORDER BY t.indatem DESC limit 1", nativeQuery = true)
    Optional<String> findMaxChecknoByCheckdt(@Param("spuncode_id") String spuncode_id);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM TB_RP715 WHERE spuncode_id = :spuncodeId", nativeQuery = true)
    void deleteBySpuncodeId(@Param("spuncodeId") String spuncodeId);

    @Query("SELECT f.filesvnm FROM TB_RP715 f WHERE f.spuncode_id = :spuncode AND f.repyn = 'Y' ORDER BY f.INDATEM desc")
    Optional<String> findByFilesvnm(@Param("spuncode") String spuncode);
}