package mes.domain.repository;

import mes.domain.entity.TB_RP940;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TB_RP940Repository extends JpaRepository<TB_RP940, String> {
  Optional<TB_RP940> findById(String id);

  Optional<TB_RP940> findByUserid(String userid);
}