package uk.ac.ox.ndph.mts.practitioner_service.model;

public class PractitionerUserAccount {
    private String practitionerId;
    private String userAccountId;

    public String getUserAccountId() {
        return userAccountId;
    }

    public String getPractitionerId() {
        return practitionerId;
    }

    public void setPractitionerId(String practitionerId) {
        this.practitionerId = practitionerId;
    }

    public void setUserAccountId(String directoryId) {
        this.userAccountId = directoryId;
    }

    public PractitionerUserAccount(String practitionerId, String userAccountId) {
        this.practitionerId = practitionerId;
        this.userAccountId = userAccountId;
    }
}

