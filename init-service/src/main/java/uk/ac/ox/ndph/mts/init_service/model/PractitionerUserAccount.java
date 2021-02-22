package uk.ac.ox.ndph.mts.init_service.model;

public class PractitionerUserAccount implements Entity {
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

