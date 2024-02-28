package mes.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mes.domain.entity.JobRes;

@Repository
public interface JobResRepository extends JpaRepository<JobRes, Integer> {
	
	JobRes getJobResById(Integer id);

	List<JobRes> findBySourceDataPkAndSourceTableName(Integer id, String string);

	List<JobRes> findBySourceDataPkAndSourceTableNameAndMaterialIdAndIdNotIn(Integer id, String string,
			Integer material_id, List<Integer> id2);
}
