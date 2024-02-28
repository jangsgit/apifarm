package mes.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mes.domain.entity.BomComponent;

public interface BomComponentRepository extends JpaRepository<BomComponent, Integer> {
	public BomComponent getBomComponentById(int id);	
}
