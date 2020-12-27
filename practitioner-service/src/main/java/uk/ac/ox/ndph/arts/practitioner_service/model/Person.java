package uk.ac.ox.ndph.arts.practitioner_service.model;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Person implements Serializable {

    private String prefix;
    private String givenName;
    private String familyName;

    public Person(String prefix, String givenName, String familyName) {
        this.prefix = prefix;
        this.givenName = givenName;
        this.familyName = familyName;
    }
}