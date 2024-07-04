package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP750;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository 
public interface ElecSafeRepository extends JpaRepository<TB_RP750, Integer> {
	

	
}