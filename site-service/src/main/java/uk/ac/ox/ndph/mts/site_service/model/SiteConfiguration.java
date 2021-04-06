package uk.ac.ox.ndph.mts.site_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Site service configuration Model
 */
@Component
@Configuration
@Validated
@ConfigurationProperties("mts.site")
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
     * @param type the Site type
     * @param attributes the List of SiteAttributeConfiguration core attributes
     * @param custom the List of SiteAttributeConfiguration custom attributes
     * @param child the List of SiteAttributeConfiguration child / children.
     *
     */
    public SiteConfiguration(String name, String displayName, String type, List<SiteAttributeConfiguration> attributes,
                             List<SiteAttributeConfiguration> custom, List<SiteConfiguration> child) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.attributes = attributes;
        this.custom = custom;
        this.child = child;
    }

    @JsonProperty(required = true)
    @NotBlank
    private String name;
    @JsonProperty(required = true)
    @NotBlank
    private String displayName;
    @JsonProperty(required = true)
    @NotBlank
    private String type;
    @JsonProperty(required = true)
    @NotEmpty
    private List<@Valid SiteAttributeConfiguration> attributes;
    private List<@Valid SiteAttributeConfiguration> custom;
    private List<@Valid SiteConfiguration> child;

    /**
     * Returns the name associated with the SiteConfiguration.
     * @return name the SiteConfiguration name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the SiteConfiguration.
     * @param name the SiteConfiguration name
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the displayName associated with the SiteConfiguration.
     * @return displayName the SiteConfiguration displayName.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the displayName of the SiteConfiguration.
     * @param displayName the SiteConfiguration displayName
     *
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the type associated with the SiteConfiguration.
     * @return type the SiteConfiguration displayName.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the SiteConfiguration.
     * @param type the SiteConfiguration type
     *
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the attributes associated with the SiteConfiguration.
     * @return attributes the SiteConfiguration List of attributes.
     */
    public List<SiteAttributeConfiguration> getAttributes() {
        return attributes;
    }

    /**
     * Sets the list of attributes of the SiteConfiguration.
     * @param attributes the SiteConfiguration List of attributes.
     *
     */
    public void setAttributes(List<SiteAttributeConfiguration> attributes) {
        this.attributes = attributes;
    }

    public List<SiteAttributeConfiguration> getCustom() {
        return custom;
    }

    public void setCustom(List<SiteAttributeConfiguration> custom) {
        this.custom = custom;
    }

    /**
     * Returns the child / children associated with the SiteConfiguration.
     * @return child the SiteConfiguration List of child.
     */
    public List<SiteConfiguration> getChild() {
        return child;
    }

    /**
     * Sets the list of child of the SiteConfiguration.
     * @param child the SiteConfiguration List of child.
     *
     */
    public void setChild(List<SiteConfiguration> child) {
        this.child = child;
    }

    /**
     * toString method
     *
     */
    @Override
    public String toString() {
        return "SiteConfiguration{"
                + "name='" + name + '\''
                + ", displayName='" + displayName + '\''
                + ", type='" + type + '\''
                + ", attributes=" + attributes
                + ", custom=" + custom
                + ", child=" + child
                + '}';
    }

}
