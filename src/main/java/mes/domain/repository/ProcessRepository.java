package mes.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mes.domain.entity.Process;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Integer> {
	
	Optional<Process> findByName(String name);
	Optional<Process> findByCode (String code);
	
	Process getProcessById(Integer id);

}
