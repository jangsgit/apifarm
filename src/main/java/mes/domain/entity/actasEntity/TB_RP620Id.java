package mes.domain.entity.actasEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP620Id implements Serializable {

    private String spworkcd;


    private String spcompcd;


    private String spplancd;


    private String standqy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TB_RP620Id that = (TB_RP620Id) o;
        return Objects.equals(spworkcd, that.spworkcd) &&
                Objects.equals(spcompcd, that.spcompcd) &&
                Objects.equals(spplancd, that.spplancd) &&
                Objects.equals(standqy, that.standqy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spworkcd, spcompcd, spplancd, standqy);
    }
}
