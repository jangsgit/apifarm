package mes.domain.entity.actasEntity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_RP910")
public class TB_RP910 {

   @Column(name="groupcd", length = 3)
   private String groupcd;

   @Id
   @Column(name="itemcd", length = 10)
   private String itemcd;

   @Column(name="itemnm", length = 50)
    private String itemnm;

   @Column(name="itemrm", length = 50)
   private String itemrm;

   @Column(name="ordno")
   private int ordno;

   @Column(name="indatem")
   private Timestamp indatem;

    @Column(name="inuserid")
    private String inuserid;

    @Column(name="inusernm")
    private String inusernm;

}
