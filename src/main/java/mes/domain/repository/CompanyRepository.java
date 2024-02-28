package mes.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mes.domain.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
	
	List<Company> findByName(String name);
	
	Company getCompnayById(Integer id);
	
	Company getCompanyById(Integer id);
	
}
