package uk.ac.ox.ndph.mts.init_service.exception;

public class DependentServiceException extends RuntimeException {

    public DependentServiceException(String message) {
        super(message);
    }
}
