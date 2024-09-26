package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_RP815;
import mes.domain.entity.actasEntity.TB_RP815Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.List;


public interface TB_RP815Repository extends JpaRepository<TB_RP815, TB_RP815Id> {

  @Query(value = "SELECT t.checkseq FROM TB_RP815 t WHERE t.spuncode_id = :spuncode_id ORDER BY t.indatem DESC limit 1", nativeQuery = true)
  Optional<String> findMaxChecknoByCheckdt(@Param("spuncode_id") String spuncode_id);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM TB_RP815 WHERE spuncode_id = :spuncodeId AND checkseq = :checkseq", nativeQuery = true)
  void deleteBySpuncodeIdAAndCheckseq(@Param("spuncodeId") String spuncodeId, @Param("checkseq") String checkseq);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM TB_RP815 t WHERE  t.spuncode_id IN :spuncode")
  void deleteBySpuncodeId(@Param("spuncode") List<String> spuncode);
}