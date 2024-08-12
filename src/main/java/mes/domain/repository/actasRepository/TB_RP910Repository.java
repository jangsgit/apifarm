package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_RP910;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TB_RP910Repository extends JpaRepository<TB_RP910,String> {

    List<TB_RP910> findByItemcdAndGroupcdIsNull(String groupCode);

    // itemcd와 groupcd가 각각 주어진 itemcd 와 groupcd와 일치하는 tb_rp910 엔티티 리스트를 반환
    List<TB_RP910> findByItemcdAndGroupcd(String itemcd, String groupcd);

    // 조건에 맞는 단일 엔티티 반환
    TB_RP910 getTbRp910ByItemcd(String itemcd);

    // 조건에 맞는 여러 엔티티 반환
    List<TB_RP910> findByItemcd(String itemcd);

}
