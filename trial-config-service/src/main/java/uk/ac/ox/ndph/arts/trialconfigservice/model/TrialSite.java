package uk.ac.ox.ndph.arts.trialconfigservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Audited
public class TrialSite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trialsite_sequence")
    @SequenceGenerator(name = "trialsite_sequence", sequenceName = "trialsite_sequence", allocationSize = 1)
    @Column
    private Long id;

    private String siteName;

    public enum SiteType {//expand this enum in future story about configuring site types
        CCO, REGION
    }


    private SiteType siteType;

    @Column
    private String FHIROrganizationId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "trial_trial_id")
    private Trial trial;

    @OneToOne(cascade=CascadeType.ALL, mappedBy="trialSite") /* This is a dummy relationship - it won't be a direct 1:1 here but will be mediated by Roles.  Dummied for the purpose of story 170. */
    private Person user;

    @Column
    @CreatedDate
    private LocalDateTime modifiedTime;

    @Column
    @CreatedBy
    private String modifiedBy;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public SiteType getSiteType() {
        return siteType;
    }

    public void setSiteType(SiteType siteType) {
        this.siteType = siteType;
    }

    public String getFHIROrganizationId() {
        return FHIROrganizationId;
    }

    public void setFHIROrganizationId(String FHIROrganizationId) {
        this.FHIROrganizationId = FHIROrganizationId;
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

    public TrialSite(){}

    public TrialSite(SiteType siteType){
        this.siteType=siteType;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }
}
