package uk.ac.ox.ndph.mts.roleserviceclient;

import java.util.List;

public interface EntityServiceClient {

    boolean entityIdExists(String entityId) throws RestException;

    List<RoleDTO> getRolesByIds(List<String> roleIds);
}
