package mes.domain.repository;

import mes.domain.entity.actasEntity.TB_RP725;
import mes.domain.entity.actasEntity.TB_RP725_PK;
import mes.domain.entity.actasEntity.TB_RP726;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository 
public interface TB_RP726Repository extends JpaRepository<TB_RP726, Integer> {
    List<TB_RP726> findAllBySpworkcdAndSpcompcdAndSpplancdAndCheckdtAndCheckno(
            String spworkcd, String spcompcd, String spplancd, String checkdt, String checkno);
}