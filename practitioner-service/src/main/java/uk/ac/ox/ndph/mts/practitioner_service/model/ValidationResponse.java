package uk.ac.ox.ndph.mts.practitioner_service.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * response from validation
 */
@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ValidationResponse {
    private boolean isValid;
    private String errorMessage;
}
