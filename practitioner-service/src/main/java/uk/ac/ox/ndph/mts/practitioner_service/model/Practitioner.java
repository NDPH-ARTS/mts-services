package uk.ac.ox.ndph.mts.practitioner_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Practitioner {
    public static final String USERACCOUNTID_IDENTIFIER_NAME = "ID_PROVIDER_USER_ACCOUNT_IDENTIFIER";
    private final String id;
    private final String prefix;
    private final String givenName;
    private final String familyName;
    private String userAccountId;

    @JsonCreator
    public Practitioner(final String id, final String prefix, final String givenName, final String familyName, 
        final String userAccountId) {
        this.id = id;
        this.prefix = prefix;
        this.givenName = givenName;
        this.familyName = familyName;
        this.userAccountId = userAccountId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getId() {
        return id;
    }

    public void setUserAccountId(final String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getUserAccountId() {
        return userAccountId;
    }
}
