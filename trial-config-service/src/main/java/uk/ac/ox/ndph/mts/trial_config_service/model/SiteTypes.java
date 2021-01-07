package uk.ac.ox.ndph.mts.trial_config_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;

@Entity
@Audited
public class SiteTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_types_sequence")
    @SequenceGenerator(name = "site_types_sequence", sequenceName = "site_types_sequence", allocationSize = 1)
    @Column
    Integer id;

    String siteName;

    String siteDescription;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "trial_id")
    private Trial trial;

    @Column
    @CreatedDate
    private LocalDateTime modifiedTime;

    @Column
    @CreatedBy
    private String modifiedBy;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteDescription() {
        return siteName;
    }

    public void setSiteDescription(String siteDescription) {
        this.siteDescription = siteDescription;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Trial getTrial() {
        return trial;
    }

    public void setTrial(Trial trial) {
        this.trial = trial;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
