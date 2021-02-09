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
class RoleAssignmentConverterTests {
    RoleAssignmentConverter roleAssignmentConverter = new RoleAssignmentConverter();

    @Test
    void TestConvert_WhenValidEntity_ConvertToPractitionerRoleCorrectly(){
        RoleAssignment roleAssignment = new RoleAssignment("practitionerId","siteId","roleId");
        var practitionerRole = roleAssignmentConverter.convert(roleAssignment);

        Assertions.assertAll(
                ()-> Assertions.assertEquals(roleAssignment.getPractitionerId(), extractReferenceId(practitionerRole.getPractitioner())),
                ()-> Assertions.assertEquals(roleAssignment.getSiteId(), extractReferenceId(practitionerRole.getOrganization())),
                ()-> Assertions.assertEquals(roleAssignment.getRoleId(), practitionerRole.getCode().get(0).getText())
        );
    }

    @Test
    void TestConvertList_WhenValidEntityList_ConvertToPractitionerRoleListCorrectly() {
        List<RoleAssignment> roleAssignmentList = List.of(
            new RoleAssignment("practitionerId1","siteId1","roleId1"),
            new RoleAssignment("practitionerId2","siteId2","roleId2"));


        var practitionerRoleList = roleAssignmentConverter.convertList(roleAssignmentList);

        Assertions.assertAll(
                ()-> Assertions.assertEquals(roleAssignmentList.get(0).getPractitionerId(), extractReferenceId(practitionerRoleList.get(0).getPractitioner())),
                ()-> Assertions.assertEquals(roleAssignmentList.get(0).getSiteId(), extractReferenceId(practitionerRoleList.get(0).getOrganization())),
                ()-> Assertions.assertEquals(roleAssignmentList.get(0).getRoleId(), practitionerRoleList.get(0).getCode().get(0).getText()),
                ()-> Assertions.assertEquals(roleAssignmentList.get(1).getPractitionerId(), extractReferenceId(practitionerRoleList.get(1).getPractitioner())),
                ()-> Assertions.assertEquals(roleAssignmentList.get(1).getSiteId(), extractReferenceId(practitionerRoleList.get(1).getOrganization())),
                ()-> Assertions.assertEquals(roleAssignmentList.get(1).getRoleId(), practitionerRoleList.get(1).getCode().get(0).getText())
        );
    }

    private String extractReferenceId(Reference reference) {
        var id = reference.getReference();
        id = id.substring(id.lastIndexOf('/') + 1);
        return id;
    }


}
