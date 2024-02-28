package mes.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mes.domain.entity.TestMastMat;

@Repository
public interface TestMastMatRepository extends JpaRepository<TestMastMat, Integer>{

	TestMastMat getTestMastMatById(Integer id);

}
