package uk.ac.ox.ndph.mts.practitioner_service.model;

public class Practitioner {

    private final String prefix;
    private final String givenName;
    private final String familyName;

    public Practitioner(final String prefix, final String givenName, final String familyName) {
        this.prefix = prefix;
        this.givenName = givenName;
        this.familyName = familyName;
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
}
