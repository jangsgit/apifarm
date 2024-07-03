package mes.domain.repository;

import mes.domain.entity.ElecSafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository 
public interface ElecSafeRepository extends JpaRepository<ElecSafe, Integer> {
	

	
}