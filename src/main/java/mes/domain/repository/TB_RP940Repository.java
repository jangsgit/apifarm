package mes.domain.repository;

import mes.domain.entity.TB_RP940;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface TB_RP940Repository extends JpaRepository<TB_RP940, String> {
  Optional<TB_RP940> findById(String id);

  Optional<TB_RP940> findByUserid(String userid);

    void deleteByUserid(String param);



  @Modifying
  @Query("UPDATE TB_RP940 t SET t.appflag = 'Y', t.appdatem = :appdatem WHERE t.userid = :userid")
  void updateApprflagToYByUserid(@Param("userid") String userid, @Param("appdatem") OffsetDateTime appdatem);

  @Modifying
  @Query("UPDATE TB_RP940 t SET t.appflag = :appflag, t.appdatem = null WHERE t.userid = :userid")
  void updateApprflagToYByUserid(@Param("userid") String userid, @Param("appflag") String appflag);

}