package mes.domain.repository;

import mes.domain.entity.TB_RP980;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TB_RP980Repository extends JpaRepository<TB_RP980, String> {

    // 기본적으로 제공되는 findAll 메서드(Sort)
    List<TB_RP980>findAll(Sort sort);

//    Optional<TB_RP980> findByEmcontno(String emcontno);

    TB_RP980 getByEmcontno(String emcontno);

    List<TB_RP980> findByEmconcomp (String emconcomp);

    List<TB_RP980> findByEmconmno (String emconmno);

    List<TB_RP980> findByEmconcompAndEmconmno (String emconcomp, String emconmno);

    @Query(value = "SELECT MAX(CAST(emcontno AS bigint)) FROM TB_RP980 WHERE emcontno ~ '^[0-9]+$'", nativeQuery = true)
    Optional<Long> findMaxEmcontno();

}
