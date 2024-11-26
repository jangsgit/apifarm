package mes.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP945Id implements Serializable {


    private String userid;
    private String askseq;

    // Getters, setters, equals, hashCode
    // (생략)
}
