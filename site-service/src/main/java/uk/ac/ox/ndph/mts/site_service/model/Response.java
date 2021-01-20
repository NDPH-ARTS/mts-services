package uk.ac.ox.ndph.mts.site_service.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A response from site service
 */
@Component
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String id;
}
