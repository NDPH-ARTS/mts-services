package uk.ac.ox.ndph.mts.trial_config_service.model;

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
    /*
    String rather than long is required.
    https://ndph-arts.atlassian.net/wiki/spaces/ARTS/pages/74187670/Trial+configuration+data
     */
    private String id;

    @Column
    private String trialName;

    @Column
    private Status status;

    @Column
    private String fhirOrganizationId;

    @OneToMany(mappedBy = "trial", cascade = CascadeType.ALL)
    private List<TrialSite> trialSites;

    @Transient
    private List<Role> roles;

    @Column
    @CreatedDate
    private LocalDateTime modifiedTime;

    @Column
    @CreatedBy
    private String modifiedBy;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public enum Status {
        IN_CONFIGURATION //expand this enum in future future story about moving from in-config to launched state
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFhirOrganizationId() {
        return fhirOrganizationId;
    }

    public void setFhirOrganizationId(String fhirOrganizationId) {
        this.fhirOrganizationId = fhirOrganizationId;
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
