package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP920;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository 
public interface TB_RP920Repository extends JpaRepository<TB_RP920, Integer> {

    @Query(value = "SELECT MAX(t.spplancd) FROM TB_RP920 t WHERE t.spworkcd = :spworkcd AND t.spcompcd = :spcompcd", nativeQuery = true)
    Optional<String> findMaxChecknoBySpplancd(@Param("spworkcd") String spworkcd,
                                              @Param("spcompcd") String spcompcd);
	
}