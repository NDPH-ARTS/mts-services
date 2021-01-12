package uk.ac.ox.ndph.mts.role_service.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_sequence")
    @SequenceGenerator(name = "role_sequence", sequenceName = "role_sequence", allocationSize = 1)
    @Column
    @Getter
    @Setter
    Integer id;

    @Getter
    @Setter
    @NotBlank
    String roleName;

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
