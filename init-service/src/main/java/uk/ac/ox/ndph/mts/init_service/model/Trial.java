package uk.ac.ox.ndph.mts.init_service.model;

import java.util.List;

public class Trial {
    private List<Practitioner> persons;
    private List<Site> sites;
    private List<Role> roles;
    private String trialName;

    public List<Practitioner> getPersons() {
        return persons;
    }

    public void setPersons(List<Practitioner> persons) {
        this.persons = persons;
    }

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
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
