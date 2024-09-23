package mes.domain.repository.actasRepository;


import mes.domain.entity.actasEntity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<CardEntity, Integer> {

    List<CardEntity> findAllByOrderByOrderNumAsc();

}
