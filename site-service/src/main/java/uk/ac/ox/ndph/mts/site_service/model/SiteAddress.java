package uk.ac.ox.ndph.mts.site_service.model;

import org.springframework.util.StringUtils;

/**
 * Address Model - Common could be UK, US, encapsulates common address attributes
 */
public class SiteAddress {
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;
    private String city;
    private String country;
    private String postcode;

    public SiteAddress(String address1, String address2, String address3, String address4,
                       String address5, String city, String country, String postcode) {
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
        this.address5 = address5;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public String getAddress5() {
        return address5;
    }

    public void setAddress5(String address5) {
        this.address5 = address5;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public boolean checkEmptyOrNull() {
        return !StringUtils.hasText(this.address1)
                && !StringUtils.hasText(this.address2)
                && !StringUtils.hasText(this.address3)
                && !StringUtils.hasText(this.address4)
                && !StringUtils.hasText(this.address5)
                && !StringUtils.hasText(this.city)
                && !StringUtils.hasText(this.country)
                && !StringUtils.hasText(this.postcode);
    }
}
