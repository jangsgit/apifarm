package mes.domain.entity.actasEntity;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP710Id implements Serializable {


    private String spworkcd;


    private String spcompcd;


    private String spplancd;


    private String checkdt;


    private String checkno;


    private String spuncode;

    // Getters, setters, equals, hashCode
    // (생략)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TB_RP710Id that = (TB_RP710Id) o;
        return Objects.equals(spworkcd, that.spworkcd) &&
                Objects.equals(spcompcd, that.spcompcd) &&
                Objects.equals(spplancd, that.spplancd) &&
                Objects.equals(checkdt, that.checkdt) &&
                Objects.equals(checkno, that.checkno) &&
                Objects.equals(spuncode, that.spuncode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spworkcd, spcompcd, spplancd, checkdt, checkno, spuncode);
    }
}
