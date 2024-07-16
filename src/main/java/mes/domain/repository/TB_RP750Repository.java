package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP750;
import mes.domain.entity.actasEntity.TB_RP750_PK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository 
public interface TB_RP750Repository extends JpaRepository<TB_RP750, TB_RP750_PK> {


}