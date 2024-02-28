package mes.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mes.domain.entity.EquRun;


@Repository
public interface EquRunRepository extends JpaRepository<EquRun, Integer>{

	EquRun getEquRunById(Integer id);

}
