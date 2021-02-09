package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)

public class RoleAssignmentConverterTests {
    RoleAssignmentConverter roleAssignmentConverter = new RoleAssignmentConverter();

    @Test
    void TestConvert_WhenValidEntity_ConvertToPractitionerRoleCorrectly(){
        RoleAssignment roleAssignment = new RoleAssignment("practitionerId","siteId","roleId");
        var practitionerRole = roleAssignmentConverter.convert(roleAssignment);

        Assertions.assertAll(
                ()-> Assertions.assertEquals(extractReferenceId(practitionerRole.getPractitioner()), roleAssignment.getPractitionerId()),
                ()-> Assertions.assertEquals(extractReferenceId(practitionerRole.getOrganization()), roleAssignment.getSiteId()),
                ()-> Assertions.assertEquals(practitionerRole.getCode().get(0).getText(), roleAssignment.getRoleId())
        );
    }

    @Test
    void TestConvertList_WhenValidEntityList_ConvertToPractitionerRoleListCorrectly() {
        List<RoleAssignment> roleAssignmentList = List.of(
            new RoleAssignment("practitionerId1","siteId1","roleId1"),
            new RoleAssignment("practitionerId2","siteId2","roleId2"));


        var practitionerRoleList = roleAssignmentConverter.convertList(roleAssignmentList);

        Assertions.assertAll(
                ()-> Assertions.assertEquals(extractReferenceId(practitionerRoleList.get(0).getPractitioner()), roleAssignmentList.get(0).getPractitionerId()),
                ()-> Assertions.assertEquals(extractReferenceId(practitionerRoleList.get(0).getOrganization()), roleAssignmentList.get(0).getSiteId()),
                ()-> Assertions.assertEquals(practitionerRoleList.get(0).getCode().get(0).getText(), roleAssignmentList.get(0).getRoleId()),
                ()-> Assertions.assertEquals(extractReferenceId(practitionerRoleList.get(1).getPractitioner()), roleAssignmentList.get(1).getPractitionerId()),
                ()-> Assertions.assertEquals(extractReferenceId(practitionerRoleList.get(1).getOrganization()), roleAssignmentList.get(1).getSiteId()),
                ()-> Assertions.assertEquals(practitionerRoleList.get(1).getCode().get(0).getText(), roleAssignmentList.get(1).getRoleId())
        );
    }

    private String extractReferenceId(Reference reference) {
        var id = reference.getReference();
        id = id.substring(id.lastIndexOf('/') + 1);
        return id;
    }


}
