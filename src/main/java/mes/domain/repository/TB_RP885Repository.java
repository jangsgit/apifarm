package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP880_PK;
import mes.domain.entity.actasEntity.TB_RP885;
import mes.domain.entity.actasEntity.TB_RP885_PK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository 
public interface TB_RP885Repository extends JpaRepository<TB_RP885, TB_RP885_PK> {

    @Query(value = "SELECT MAX(t.contseq) FROM TB_RP885 t where t.checkdt = :checkdt and t.contdt = :contdt", nativeQuery = true)
    Optional<String> findMaxContseq(@Param("checkdt") String checkdt,
                                    @Param("contdt") String contdt);

    List<TB_RP885> findAllById_CheckdtAndId_Contdt(
            String checkdt, String contdt);

    @Query("SELECT DISTINCT t.contsequsr FROM TB_RP885 t WHERE LOWER(t.contsequsr) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findContsequsrByQuery(@Param("query") String query);
}