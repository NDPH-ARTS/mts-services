package uk.ac.ox.ndph.mts.site_service.model;

public class SiteNameDTO {
    private String siteId;
    private String siteName;

    public SiteNameDTO(String siteId, String siteName) {
        this.siteId = siteId;
        this.siteName = siteName;
    }


    public String getSiteId() {
        return siteId;
    }

    public String getSiteName() {
        return siteName;
    }
}
