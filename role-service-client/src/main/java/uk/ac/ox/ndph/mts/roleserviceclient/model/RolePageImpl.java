package uk.ac.ox.ndph.mts.roleserviceclient.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@JsonIgnoreProperties("pageable")
public class RolePageImpl extends PageImpl<RoleDTO> {

    @JsonCreator
    public RolePageImpl(@JsonProperty("content") final List<RoleDTO> content) {
        super(content);
    }

}
