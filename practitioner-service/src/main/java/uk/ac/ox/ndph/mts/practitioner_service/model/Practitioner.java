package uk.ac.ox.ndph.arts.practitioner_service.model;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Practitioner implements Serializable {

    private String prefix;
    private String givenName;
    private String familyName;
    
}