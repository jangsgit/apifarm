package mes.domain.entity.actasEntity;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;


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
}
