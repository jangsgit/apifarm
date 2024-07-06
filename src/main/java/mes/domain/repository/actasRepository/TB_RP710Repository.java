package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.entity.actasEntity.TB_RP710Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TB_RP710Repository extends JpaRepository<TB_RP710, TB_RP710Id> {


  @Query(value = "SELECT t.checkno FROM TB_RP710 t WHERE t.checkdt = :checkdtconvertvalue ORDER BY t.checkno DESC limit 1", nativeQuery = true)
  Optional<String> findMaxChecknoByCheckdt(@Param("checkdtconvertvalue") String checkdtconvertvalue);

  void deleteBySpuncode(String spuncode);


}