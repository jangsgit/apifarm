package mes.domain.entity.actasEntity;

import java.io.Serializable;
import java.util.Objects;

public class TB_RP621Id implements Serializable {

    private String spworkcd;

    private String spcompcd;


    private String spplancd;


    private String standqy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TB_RP621Id that = (TB_RP621Id) o;
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
