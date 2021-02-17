package uk.ac.ox.ndph.mts.roleserviceclient;

import java.util.List;

public interface RoleServiceClient {

    List<RoleDTO> getRolesByIds(List<String> roleIds);

}
