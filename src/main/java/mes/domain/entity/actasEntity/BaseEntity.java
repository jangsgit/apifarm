package mes.domain.entity.actasEntity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BaseEntity {

    @CreatedDate
    @Column(name = "indatem", updatable = false)
    private LocalDateTime INDATEM;   //입력일시

    @Column(name = "spworknm")
    private String spworknm;

    @Column(name = "spcompnm")
    private String spcompnm;

    @Column(name = "spplannm")
    private String spplannm;
}
