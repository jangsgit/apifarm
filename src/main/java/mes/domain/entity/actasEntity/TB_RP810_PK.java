package mes.domain.entity.actasEntity;

import lombok.*;
import org.apache.poi.ss.formula.functions.Offset;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP810_PK implements Serializable {

    private String spworkcd;
    private String spcompcd;
    private String spplancd;
    private String srnumber;
    private LocalDateTime servicertm;
    private LocalDateTime serviceftm;
    private String spuncode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TB_RP810_PK that = (TB_RP810_PK) o;
        return Objects.equals(spworkcd, that.spworkcd) &&
                Objects.equals(spcompcd, that.spcompcd) &&
                Objects.equals(spplancd, that.spplancd) &&
                Objects.equals(srnumber, that.srnumber) &&
                Objects.equals(servicertm, that.servicertm) &&
                Objects.equals(serviceftm, that.serviceftm) &&
                Objects.equals(spuncode, that.spuncode);

    }

    @Override
    public int hashCode() {
        return Objects.hash(spworkcd, spcompcd, spplancd, srnumber, servicertm, serviceftm, spuncode);
    }

}
