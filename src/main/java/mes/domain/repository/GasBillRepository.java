package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP410;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GasBillRepository extends JpaRepository<TB_RP410,String> {
//    List<TB_RP410> findAll(Sort sort);
//
//    TB_RP410 findBystandym(String standym);
//
//    List<TB_RP410> findbystandymBetween(String startDate, String endDate, Sort sort);


}
