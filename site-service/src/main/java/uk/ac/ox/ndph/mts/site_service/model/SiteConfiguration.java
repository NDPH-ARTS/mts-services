package uk.ac.ox.ndph.mts.site_service.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Site service configuration Model
 */
@Component
@Getter
@Setter
public class SiteConfiguration {

    /**
     * SiteConfiguration Constructor with no parameters
     *
     */
    public SiteConfiguration() {

    }

    /**
     * Site Constructor with two parameters
     *
     * @param name the Site name
     * @param displayName the Site displayName
     * @param attributes the List of SiteAttributeConfiguration attributes
     *
     */
    public SiteConfiguration(String name, String displayName, List<SiteAttributeConfiguration> attributes) {
        this.name = name;
        this.displayName = displayName;
        this.attributes = attributes;
    }

    private String name;
    private String displayName;
    private List<SiteAttributeConfiguration> attributes;


    /**
     * toString method
     *
     */
    @Override
    public String toString() {
        return "SiteConfiguration{"
                + "name='" + name + '\''
                + ", displayName='" + displayName + '\''
                + ", attributes=" + attributes
                + '}';
    }

}
