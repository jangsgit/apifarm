package mes.domain.repository;


import mes.domain.entity.actasEntity.TB_RP310;
import mes.domain.entity.actasEntity.TB_RP310_Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TB_RP310Repository extends JpaRepository<TB_RP310, TB_RP310_Id> {


}
