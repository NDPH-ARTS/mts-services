package uk.ac.ox.ndph.mts.practitionerserviceclient.model;

public class PractitionerUserAccountDTO implements Entity {
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

    public PractitionerUserAccountDTO(String practitionerId, String userAccountId) {
        this.practitionerId = practitionerId;
        this.userAccountId = userAccountId;
    }
}

