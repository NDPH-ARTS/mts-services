package uk.ac.ox.ndph.mts.practitioner_service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

/**
 * Practitioner service configuration Model
 */
@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PractitionerConfiguration {
    private String name;
    private String displayName;
    private List<PractitionerAttributeConfiguration> attributes;
}
