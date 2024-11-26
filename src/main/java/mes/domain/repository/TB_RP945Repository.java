package mes.domain.repository;

import mes.domain.entity.TB_RP945;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;

import java.util.Optional;


public interface TB_RP945Repository extends JpaRepository<TB_RP945, String> {

    @Query("SELECT MAX(t.askseq) FROM TB_RP945 t")
    String findMaxAskSeq();

    void deleteByUserid(String param);

    List<TB_RP945> findByUserid(String userid);
}