package uk.ac.ox.ndph.mts.site_service.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A response from site service
 */
@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String id;
}
