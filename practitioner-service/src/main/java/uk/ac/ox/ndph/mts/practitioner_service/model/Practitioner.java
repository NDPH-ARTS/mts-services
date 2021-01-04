package uk.ac.ox.ndph.mts.practitioner_service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Practitioner {

    private String prefix;
    private String givenName;
    private String familyName;

}
