package uk.ac.ox.ndph.mts.init_service.model;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Practitioner Model
 */
public class Practitioner implements Entity {
    private String id;
    private String prefix;
    @NotBlank
    private String givenName;
    @NotBlank
    private String familyName;
    private List<@NotBlank String> roles;
    private String userAccount;

    public Practitioner(String id, String prefix, String givenName, String familyName, String userAccountId) {
        this.id = id;
        this.prefix = prefix;
        this.givenName = givenName;
        this.familyName = familyName;
        userAccount = userAccountId;
    }

    public Practitioner() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public String toString() {
        return String.format(
                "Practitioner{prefix='%s', "
                        + "givenName='%s', "
                        + "familyName='%s', "
                        + "userAccount='%s'}",
                prefix,
                givenName,
                familyName,
                userAccount);
    }
}
