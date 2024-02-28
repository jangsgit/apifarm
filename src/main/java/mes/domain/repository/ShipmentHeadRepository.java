package mes.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mes.domain.entity.ShipmentHead;

@Repository
public interface ShipmentHeadRepository extends JpaRepository<ShipmentHead, Integer>{

	ShipmentHead getShipmentHeadById(Integer id);

}
