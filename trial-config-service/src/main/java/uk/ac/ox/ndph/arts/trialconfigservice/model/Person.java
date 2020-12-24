package uk.ac.ox.ndph.arts.trialconfigservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Audited
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_sequence")
    @SequenceGenerator(name = "person_sequence", sequenceName = "person_sequence", allocationSize = 1)
    @Column
    private Long id;

    @Column (unique=true)
    private String azureOid;

    @JsonIgnore
    @OneToOne  /* This is a dummy relationship - it won't be a direct 1:1 here but will be mediated by Roles.  Dummied for the purpose of story 170. */
    @JoinColumn(name = "trialsite_id")
    private TrialSite trialSite;

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

    public String getAzureOid() {
        return azureOid;
    }

    public void setAzureOid(String azureOid) {
        this.azureOid = azureOid;
    }

    public TrialSite getTrialSite() {
        return trialSite;
    }

    public void setTrialSite(TrialSite trialSite) {
        this.trialSite = trialSite;
    }

    public Person(String azureOid, LocalDateTime modifiedTime, String modifiedBy){
        this.azureOid=azureOid;
        this.modifiedTime = modifiedTime;
        this.modifiedBy = modifiedBy;
    }
    public Person(){}



}
