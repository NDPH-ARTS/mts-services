package uk.ac.ox.ndph.mts.site_service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

/**
 * Site service configuration Model
 */
@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SiteConfiguration {
    private String name;
    private String displayName;
    private List<SiteAttributeConfiguration> attributes;
}
