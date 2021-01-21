package uk.ac.ox.ndph.mts.role_service.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@MappedSuperclass
public abstract class AuditedEntity {


    @Column
    @CreatedDate
    @Getter
    @Setter
    private LocalDateTime createdDateTime;

    @Column
    @CreatedBy
    @Getter
    @Setter
    private String createdBy;

    @Column
    @LastModifiedDate
    @Getter
    @Setter
    private LocalDateTime modifiedDateTime;

    @Column
    @LastModifiedBy
    @Getter
    @Setter
    private String modifiedBy;


}
