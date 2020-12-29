package uk.ac.ox.ctsu.arts.sitetypeservice.model;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Audited
public class SiteType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_type_sequence")
    @SequenceGenerator(name = "site_type_sequence", sequenceName = "site_type_sequence", allocationSize = 1)
    @Column
    private Long id;

    @Column
    private String name;
    @Column
    private String description;

    @Column
    @CreatedDate
    private LocalDateTime changedWhen;
    @Column
    @CreatedBy
    private String changedWho;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getChangedWhen() {
        return changedWhen;
    }

    public void setChangedWhen(LocalDateTime changedWhen) {
        this.changedWhen = changedWhen;
    }

    public String getChangedWho() {
        return changedWho;
    }

    public void setChangedWho(String changedWho) {
        this.changedWho = changedWho;
    }
}
