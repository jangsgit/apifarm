package mes.domain.entity.actasEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP815Id implements Serializable {

    private String spworkcd;
    private String spcompcd;
    private String spplancd;
    private String checkseq;
    private String spuncode_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TB_RP815Id that = (TB_RP815Id) o;
        return Objects.equals(spworkcd, that.spworkcd) &&
                Objects.equals(spcompcd, that.spcompcd) &&
                Objects.equals(spplancd, that.spplancd) &&
                Objects.equals(checkseq, that.checkseq) &&
                Objects.equals(spuncode_id, that.spuncode_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spworkcd, spcompcd, spplancd, checkseq, spuncode_id);
    }
}
