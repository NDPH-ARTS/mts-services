package uk.ac.ox.ndph.mts.init_service.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.ac.ox.ndph.mts.roleserviceclient.model.RoleDTO;
import uk.ac.ox.ndph.mts.siteserviceclient.model.SiteDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Component
@Configuration
@Validated
@ConfigurationProperties("mts.trial")
public class Trial {
    private List<Practitioner> persons;
    private List<SiteDTO> sites;
    private List<RoleDTO> roles;
    @NotEmpty
    private List<@Valid Practitioner> persons;
    @NotEmpty
    private List<@Valid Site> sites;
    @NotEmpty
    private List<@Valid RoleDTO> roles;
    @NotBlank
    private String trialName;

    public List<Practitioner> getPersons() {
        return persons;
    }

    public void setPersons(List<Practitioner> persons) {
        this.persons = persons;
    }

    public List<SiteDTO> getSites() {
        return sites;
    }

    public void setSites(List<SiteDTO> sites) {
        this.sites = sites;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public String getTrialName() {
        return trialName;
    }

    public void setTrialName(String trialName) {
        this.trialName = trialName;
    }

    @Override
    public String toString() {
        return String.format("Trial{persons='%s', sites='%s', roles='%s'}", persons, sites, roles);
    }
}
