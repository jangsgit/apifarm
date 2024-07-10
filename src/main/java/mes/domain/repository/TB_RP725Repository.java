package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP725;
import mes.domain.entity.actasEntity.TB_RP820;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository 
public interface TB_RP725Repository extends JpaRepository<TB_RP725, Integer> {
	
}