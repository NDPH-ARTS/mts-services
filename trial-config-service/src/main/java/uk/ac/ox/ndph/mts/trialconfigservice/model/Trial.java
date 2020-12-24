package uk.ac.ox.ndph.mts.trialconfigservice.model;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Audited
@Entity
public class Trial {

    @Id
    @Column
    private String trialId; //String rather than long is required: https://ndph-arts.atlassian.net/wiki/spaces/ARTS/pages/74187670/Trial+configuration+data

    @Column
    private String trialName;

    @Column
    private Status status;

    @Column
    private String FHIROrganizationId;

    @OneToMany(mappedBy="trial", cascade=CascadeType.ALL)
    private List<TrialSite> trialSites;

    @Column
    @CreatedDate
    private LocalDateTime modifiedTime;

    @Column
    @CreatedBy
    private String modifiedBy;

    public enum Status {
        IN_CONFIGURATION //expand this enum in future future story about moving from in-config to launched state
    }

    public String getTrialId() {
        return trialId;
    }

    public void setTrialId(String trialId) {
        this.trialId = trialId;
    }

    public String getTrialName() {
        return trialName;
    }

    public void setTrialName(String trialName) {
        this.trialName = trialName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getFHIROrganizationId() {
        return FHIROrganizationId;
    }

    public void setFHIROrganizationId(String FHIROrganizationId) {
        this.FHIROrganizationId = FHIROrganizationId;
    }

    public List<TrialSite> getTrialSites() {
        return trialSites;
    }

    public void setTrialSites(List<TrialSite> trialSites) {

        this.trialSites = trialSites;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
