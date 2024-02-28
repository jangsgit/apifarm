package mes.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mes.domain.entity.Depart;

@Repository
public interface DepartRepository extends JpaRepository<Depart, Integer>{

	Depart getDepartById(Integer id);

}
