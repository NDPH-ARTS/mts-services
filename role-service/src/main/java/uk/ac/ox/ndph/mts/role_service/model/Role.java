package uk.ac.ox.ndph.mts.role_service.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Audited
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_sequence")
    @SequenceGenerator(name = "role_sequence", sequenceName = "role_sequence", allocationSize = 1)
    @Column
    @Getter @Setter
    Integer id;

    @Getter @Setter
    String roleName;

    @Column
    @CreatedDate
    @Getter @Setter
    private LocalDateTime createdDateTime;

    @Column
    @CreatedBy
    @Getter @Setter
    private String createdBy;

    @Column
    @LastModifiedDate
    @Getter @Setter
    private LocalDateTime modifiedDateTime;

    @Column
    @LastModifiedBy
    @Getter @Setter
    private String modifiedBy;


}
