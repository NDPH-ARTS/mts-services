package uk.ac.ox.ndph.mts.site_service.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * Address configuration Model
 */
@Component
@Configuration
@Validated
@ConfigurationProperties("mts.address")
public class AddressConfiguration {

    /**
     * AddressConfiguration Constructor with no parameters
     *
     */
    public AddressConfiguration() {

    }

    /**
     * AddressConfiguration Constructor with two parameters
     *
     * @param addressType the List of SiteAttributeConfiguration attributes
     */
    public AddressConfiguration(List<SiteAttributeConfiguration> addressType) {
        this.addressType = addressType;
    }

    private List<@Valid SiteAttributeConfiguration> addressType;

    /**
     * Returns the attributes associated with the SiteConfiguration.
     * @return attributes the SiteConfiguration List of attributes.
     */
    public List<SiteAttributeConfiguration> getAddressType() {
        return addressType;
    }

    /**
     * Sets the list of attributes of the SiteConfiguration.
     * @param addressType the SiteConfiguration List of attributes.
     *
     */
    public void setAddressType(List<SiteAttributeConfiguration> addressType) {
        this.addressType = addressType;
    }

    /**
     * toString method
     *
     */
    @Override
    public String toString() {
        return "AddressConfiguration{"
                + "addressType=" + addressType
                + '}';
    }

}
