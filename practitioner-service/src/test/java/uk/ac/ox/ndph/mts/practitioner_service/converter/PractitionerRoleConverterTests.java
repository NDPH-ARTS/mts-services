package uk.ac.ox.ndph.mts.practitioner_service.converter;

import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PractitionerRoleConverterTests {
    PractitionerRoleConverter practitionerRoleConverter = new PractitionerRoleConverter();

    @Test
    void TestConvert_WhenValidEntity_ConvertToRoleAssignmentCorrectly(){
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.setOrganization(new Reference("Organization/site_id"));
        practitionerRole.setPractitioner(new Reference("Practitioner/practitioner_id"));
        practitionerRole.addCode().setText("role_id");

        var roleAssignment =  practitionerRoleConverter.convert(practitionerRole);
        Assertions.assertAll(
                ()-> Assertions.assertEquals("site_id", roleAssignment.getSiteId()),
                ()-> Assertions.assertEquals("practitioner_id", roleAssignment.getPractitionerId()),
                ()-> Assertions.assertEquals("role_id", roleAssignment.getRoleId())
        );
    }

    @Test
    void TestConvertList_WhenValidEntityList_ConvertAllListItemsToRoleAssignmentList(){
        List<PractitionerRole> practitionerRoleList = new ArrayList<>();
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.setOrganization(new Reference("Organization/site_id1"));
        practitionerRole.setPractitioner(new Reference("Practitioner/practitioner_id1"));
        practitionerRole.addCode().setText("role_id1");
        practitionerRoleList.add(practitionerRole);
        practitionerRole = new PractitionerRole();
        practitionerRole.setOrganization(new Reference("Organization/site_id2"));
        practitionerRole.setPractitioner(new Reference("Practitioner/practitioner_id2"));
        practitionerRole.addCode().setText("role_id2");
        practitionerRoleList.add(practitionerRole);

        var roleAssignmentsList =  practitionerRoleConverter.convertList(practitionerRoleList);
        Assertions.assertAll(
                ()-> Assertions.assertEquals("site_id1", roleAssignmentsList.get(0).getSiteId()),
                ()-> Assertions.assertEquals("practitioner_id1", roleAssignmentsList.get(0).getPractitionerId()),
                ()-> Assertions.assertEquals("role_id1", roleAssignmentsList.get(0).getRoleId()),
                ()-> Assertions.assertEquals("site_id2", roleAssignmentsList.get(1).getSiteId()),
                ()-> Assertions.assertEquals("practitioner_id2", roleAssignmentsList.get(1).getPractitionerId()),
                ()-> Assertions.assertEquals("role_id2", roleAssignmentsList.get(1).getRoleId())
        );
    }
}
