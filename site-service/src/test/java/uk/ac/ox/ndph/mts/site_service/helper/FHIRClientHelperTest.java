package uk.ac.ox.ndph.mts.site_service.helper;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class FHIRClientHelperTest {

    @Mock
    FHIRClientHelper fhirClientHelperMock;

    @Mock
    Organization fhirOrganizationMock;

    @Mock
    ResearchStudy researchStudyMock;

    final String strRegion = "Region";

    @BeforeEach
    void setUp() throws Exception {

        fhirClientHelperMock = new FHIRClientHelper("http://localhost:8080");
        fhirOrganizationMock = new Organization();
        researchStudyMock = new ResearchStudy();

        fhirOrganizationMock.setName(strRegion);
        fhirOrganizationMock.addAlias(strRegion);

        researchStudyMock = new ResearchStudy();
        researchStudyMock.setStatus(ResearchStudy.ResearchStudyStatus.ACTIVE);
    }

    @Test
    void testAccessToken() {
    }

    @Test
    void testFhirURI() {
    }

    @Test
    void createResource() {
        //when(fhirClientHelperMock.createResource(fhirOrganizationMock).getId().toString()).thenReturn("123");
        //String strOrganizationId = fhirClientHelperMock.createResource(fhirOrganizationMock).getId().toString();
        //assertThat("123", equalTo(strOrganizationId));
    }

    @Test
    void updateResource() {
    }

    @Test
    void searchResource() {
    }

    @Test
    void deleteResource() {
    }

    @Test
    void testClient() {
    }

    @Test
    void testCheckCapabilities() {
    }

    @Test
    void testShow() {
    }

    @Test
    void testFindOrganizationByID() {
    }
}
