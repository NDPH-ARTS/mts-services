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
                ()-> Assertions.assertEquals(roleAssignment.getPractitionerId(), practitionerRole.getPractitioner().getReferenceElement().getIdPart()),
                ()-> Assertions.assertEquals(roleAssignment.getSiteId(), practitionerRole.getOrganization().getReferenceElement().getIdPart()),
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
                ()-> Assertions.assertEquals(roleAssignmentList.get(0).getPractitionerId(), practitionerRoleList.get(0).getPractitioner().getReferenceElement().getIdPart()),
                ()-> Assertions.assertEquals(roleAssignmentList.get(0).getSiteId(), practitionerRoleList.get(0).getOrganization().getReferenceElement().getIdPart()),
                ()-> Assertions.assertEquals(roleAssignmentList.get(0).getRoleId(), practitionerRoleList.get(0).getCode().get(0).getText()),
                ()-> Assertions.assertEquals(roleAssignmentList.get(1).getPractitionerId(), practitionerRoleList.get(1).getPractitioner().getReferenceElement().getIdPart()),
                ()-> Assertions.assertEquals(roleAssignmentList.get(1).getSiteId(), practitionerRoleList.get(1).getOrganization().getReferenceElement().getIdPart()),
                ()-> Assertions.assertEquals(roleAssignmentList.get(1).getRoleId(), practitionerRoleList.get(1).getCode().get(0).getText())
        );
    }
}
