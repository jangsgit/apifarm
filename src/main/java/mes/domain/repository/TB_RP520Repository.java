package mes.domain.repository;

import mes.domain.entity.TB_RP520;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TB_RP520Repository extends JpaRepository<TB_RP520, String> {

    TB_RP520 findByExpesym(String expesym);

    List<TB_RP520> findAll(Sort sort);

    List<TB_RP520> findByExpesym(String startDate, String endDate, Sort sort);

    boolean existsByExpesym(String expesym);


}
