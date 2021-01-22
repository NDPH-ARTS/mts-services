package uk.ac.ox.ndph.mts.site_service.helper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IParam;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;

/**
 * Hepler Class for FHIR API
 */
public class FHIRClientHelper {

    private FhirContext ctx = FhirContext.forR4();
    private volatile Object creatingClient = new Object();
    private volatile Boolean creatingClientFlag = false;
    private IGenericClient client;
    private String fhirURI;
    private String accessToken;

    /**
     *
     * @param client - The IGenericClient client
     *
     */
    public FHIRClientHelper(IGenericClient client) {
        this.client = client;
    }

    /**
     *
     * @return String - The String accessToken
     *
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     *
     * @return String - The String fhirURI
     *
     */
    public String getFhirURI() {
        return fhirURI;
    }

    /**
     *
     * @param fhirURI - The Resource resource
     * @param accessToken - The accessToken
     *
     */
    public FHIRClientHelper(String fhirURI, String accessToken) throws Exception {
        this.fhirURI = fhirURI;
        this.accessToken = accessToken;
        this.client = this.createClient(fhirURI, accessToken);
    }

    /**
     *
     * @param fhirURI - The Resource resource
     *
     */
    public FHIRClientHelper(String fhirURI) throws Exception {
        this.fhirURI = fhirURI;
        this.client = this.createClient(fhirURI);
    }

    /**
     *
     * @param resource - The Resource resource
     * @return MethodOutcome
     *
     */
    public MethodOutcome createResource(Resource resource) {
        return client.create().resource(resource).prettyPrint().encodedJson().execute();
    }

    /**
     *
     * @param resource - The Resource resource
     * @return MethodOutcome
     *
     */
    public MethodOutcome updateResource(Resource resource) {
        return client.update().resource(resource).prettyPrint().encodedJson().execute();
    }

    /**
     *
     * @param resourceName - The Resource Name
     * @param searchCriteria - The Criterion searchCriteria
     * @return IBaseOperationOutcome
     *
     */
    public String searchResource(String resourceName, ICriterion<? extends IParam> searchCriteria) {
        return  show(client.search().forResource(resourceName).where(searchCriteria)
                .returnBundle(Bundle.class).execute());
    }


    /**
     *
     * @param resource - The Resource resource
     * @return IBaseOperationOutcome
     *
     */
    public IBaseOperationOutcome deleteResource(Resource resource) {
        return (IBaseOperationOutcome) client.delete().resource(resource).prettyPrint().encodedJson().execute();
    }

    /**
     *
     * @return client
     *
     */
    public IGenericClient getClient() {
        return client;
    }

    /**
     *
     * @return
     *
     */
    public void checkCapabilities() {
        try {
            client.capabilities();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private synchronized IGenericClient createClient(String fhirURI) throws Exception {
        if (!creatingClientFlag) {
            synchronized (creatingClient) {
                creatingClientFlag = true;
                client = ctx.newRestfulGenericClient(fhirURI);
                ctx.getRestfulClientFactory().setSocketTimeout(100000);
                creatingClientFlag = false;
                creatingClient.notifyAll();
                return client;
            }
        } else {
            creatingClient.wait();
        }
        return null;
    }

    private synchronized IGenericClient createClient(String fhirURI, String accessToken) throws Exception {
        if (!creatingClientFlag) {
            synchronized (creatingClient) {
                creatingClientFlag = true;
                FhirContext ctx = FhirContext.forDstu3();
                IRestfulClientFactory clientFactory = ctx.getRestfulClientFactory();
                ctx.getRestfulClientFactory().setSocketTimeout(100);
                client = clientFactory.newGenericClient(fhirURI);
                BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(accessToken);
                client.registerInterceptor(authInterceptor);
                creatingClientFlag = false;
                creatingClient.notifyAll();
                return client;
            }
        } else {
            creatingClient.wait();
        }
        return null;
    }

    /**
     *
     * @param bundle - The Bundle bundle
     * @return String
     *
     */
    public String show(Bundle bundle) {
        return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
    }

    /**
     *
     * @param id - The Organization id
     * @return Organization
     */
    public Organization findOrganizationByID(String id) {
        return client.read().resource(Organization.class).withId(id).execute();
    }

}
