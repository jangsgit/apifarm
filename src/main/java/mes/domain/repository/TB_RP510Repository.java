package mes.domain.repository;

import mes.domain.entity.TB_RP510;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TB_RP510Repository extends JpaRepository<TB_RP510, String> {
	
	TB_RP510 findBySalesym(String salesym);
	
	// 전체 매출 데이터 조회 메소드
	List<TB_RP510> findAll(Sort sort);
	
	// 특정 날짜 범위에 따른 매출 데이터 조회 메소드
	List<TB_RP510> findBySalesymBetween(String startDate, String endDate, Sort sort);
	
	// 중복 확인
	boolean existsBySalesym(String salesym);

	// 매출 정보 삭제 메소드
//	void deleteById(Long id);

}
