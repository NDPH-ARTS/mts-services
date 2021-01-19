package uk.ac.ox.ndph.mts.site_service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Site Model
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Site {

    /**
     * Site Constructor with two parameters
     *
     * @param name the Site name
     * @param alias the Site alias
     */
    public Site(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    private String name;
    private String alias;
    private String parentFhirId;

}
