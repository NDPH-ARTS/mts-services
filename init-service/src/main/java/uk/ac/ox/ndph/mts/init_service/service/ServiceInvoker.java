package uk.ac.ox.ndph.mts.init_service.service;

import uk.ac.ox.ndph.mts.init_service.exception.DependentServiceException;
import uk.ac.ox.ndph.mts.init_service.model.Entity;

public interface ServiceInvoker {
    String send(Entity entity) throws DependentServiceException;
}
