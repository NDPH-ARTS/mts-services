package uk.ac.ox.ndph.mts.init_service.model;

import java.util.List;

/**
 * Practitioner Model
 */
public class Practitioner implements Entity {

    private String prefix;
    private String givenName;
    private String familyName;
    private List<String> roles;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return String.format(
                "Practitioner{prefix='%s', "
                + "givenName='%s', "
                + "familyName'%s'}",
                prefix,
                givenName,
                familyName);
    }
}
