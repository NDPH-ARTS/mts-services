package uk.ac.ox.ndph.mts.practitioner_service.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

/**
 * Practitioner Attribute configuration Model
 */
@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PractitionerAttribute {
    private String name;
    private String displayName;
    private String validationRegex;
}
