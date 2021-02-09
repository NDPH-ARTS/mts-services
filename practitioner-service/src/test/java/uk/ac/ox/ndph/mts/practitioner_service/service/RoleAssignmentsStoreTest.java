package uk.ac.ox.ndph.mts.practitioner_service.service;

import org.apache.commons.lang.NotImplementedException;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ox.ndph.mts.practitioner_service.converter.EntityConverter;
import uk.ac.ox.ndph.mts.practitioner_service.model.Practitioner;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleAssignment;
import uk.ac.ox.ndph.mts.practitioner_service.repository.FhirRepository;
import uk.ac.ox.ndph.mts.practitioner_service.repository.PractitionerStore;
import uk.ac.ox.ndph.mts.practitioner_service.repository.RoleAssignmentStore;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleAssignmentsStoreTest {

    @Mock
    private FhirRepository repository;

    @Mock
    private EntityConverter<RoleAssignment, PractitionerRole> roleAssignmentPractitionerRoleEntityConverter;

    @Mock
    private EntityConverter<PractitionerRole, RoleAssignment> practitionerRoleRoleAssignmentEntityConverter;

    @Test
    void TestSaveEntity_WhenValidEntity_SaveToRepositoryAndReturnGeneratedId()
    {
        //arrange
        var inputRoleAssignment = new RoleAssignment("practitionerId","siteId","roleId");
        var outputPractitioner = new org.hl7.fhir.r4.model.Practitioner();
        when(roleAssignmentPractitionerRoleEntityConverter.convert(any(RoleAssignment.class))).thenReturn(new PractitionerRole());
        when(repository.savePractitionerRole(any(org.hl7.fhir.r4.model.PractitionerRole.class))).thenReturn("123");

        //act
        RoleAssignmentStore roleAssignmentStore = new RoleAssignmentStore(repository, roleAssignmentPractitionerRoleEntityConverter, practitionerRoleRoleAssignmentEntityConverter);
        var result = roleAssignmentStore.saveEntity(inputRoleAssignment);

        //assert
        assertThat(result, equalTo("123"));
    }

    @Test
    void TestListEntitiesByUserIdentity_WhenListByUserIdentity_ReturnsListOfRoleAssignments()
    {
        //arrange
        List<PractitionerRole> practitionerRoleList = List.of(new PractitionerRole());
        List<RoleAssignment> roleAssignmentList = List.of(new RoleAssignment("practitionerId","siteId","roleId"));
        RoleAssignmentStore roleAssignmentStore = new RoleAssignmentStore(repository, roleAssignmentPractitionerRoleEntityConverter, practitionerRoleRoleAssignmentEntityConverter);
        when(repository.getPractitionerRolesByUserIdentity(any(String.class))).thenReturn(practitionerRoleList);
        when(practitionerRoleRoleAssignmentEntityConverter.convertList(practitionerRoleList)).thenReturn(roleAssignmentList);

        //act
        var result = roleAssignmentStore.listEntitiesByUserIdentity("123");

        //assert
        assertThat(result, equalTo(roleAssignmentList));

    }
}
