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

    private String name;
    private String alias;

}
