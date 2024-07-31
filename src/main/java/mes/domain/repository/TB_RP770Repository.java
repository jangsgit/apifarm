package mes.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//public interface TB_RP770Repository extends JpaRepository<TB_RP770, TB_RP770Id> {
//  @Query(value = "SELECT t.checkseq FROM TB_RP760 t WHERE t.standdt = :checkdtconvertvalue ORDER BY t.checkseq DESC limit 1", nativeQuery = true)
//  Optional<String> findMaxChecknoByCheckdt(@Param("checkdtconvertvalue") String checkdtconvertvalue);
//}