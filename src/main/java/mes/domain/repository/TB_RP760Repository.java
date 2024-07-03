package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP760;
import mes.domain.entity.actasEntity.TB_RP760Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TB_RP760Repository extends JpaRepository<TB_RP760, TB_RP760Id> {
  @Query(value = "SELECT t.checkseq FROM TB_RP760 t WHERE t.standdt = :checkdtconvertvalue ORDER BY t.checkseq DESC limit 1", nativeQuery = true)
  Optional<String> findMaxChecknoByCheckdt(@Param("checkdtconvertvalue") String checkdtconvertvalue);
}