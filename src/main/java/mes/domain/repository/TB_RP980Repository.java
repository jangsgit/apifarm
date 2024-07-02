package mes.domain.repository;

import mes.domain.entity.TB_RP980;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TB_RP980Repository extends JpaRepository<TB_RP980, String> {
    Optional<TB_RP980> findByid(String id);
}
