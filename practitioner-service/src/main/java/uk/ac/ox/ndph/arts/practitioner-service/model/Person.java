package uk.ac.ox.ndph.arts.practitiner_service.model;

public class Person {
    private String prefix;
    private String givenName;
    private String familyName;

    public Person(String prefix, String givenName, String familyName) {
        this.prefix = prefix;
        this.givenName = givenName;
        this.familyName = familyName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void SetPrefix(String prefix) {
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
}