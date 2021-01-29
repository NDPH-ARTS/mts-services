package uk.ac.ox.ndph.mts.practitioner_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import uk.ac.ox.ndph.mts.practitioner_service.model.PageableResult;
import uk.ac.ox.ndph.mts.practitioner_service.model.RoleDTO;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class TestRolesServiceBackend {

    private MockWebServer mockBackEnd;

    public void start() {
        try {
            this.mockBackEnd = new MockWebServer();
            this.mockBackEnd.start();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static TestRolesServiceBackend autoStart() {
        final TestRolesServiceBackend result = new TestRolesServiceBackend();
        result.start();
        return result;
    }

    public void shutdown() {
        if (this.mockBackEnd != null) {
            try {
                final MockWebServer server = this.mockBackEnd;
                this.mockBackEnd = null;
                server.shutdown();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public String getUrl() {
        return String.format("http://localhost:%s", mockBackEnd.getPort());
    }

    public void queueRolesResponse(final String roleId) {
        try {
            final var roleObj = new RoleDTO();
            if (roleId != null) {
                roleObj.setId(roleId);
            }
            final var response = (roleId == null) ? PageableResult.empty() : PageableResult.singleton(roleObj);
            mockBackEnd.enqueue(new MockResponse()
                    .setBody(new ObjectMapper().writeValueAsString(response))
                    .addHeader("Content-Type", "application/json"));
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void queueRoleResponse(final String roleId) {
        queueRoleResponse(roleId, 0);
    }

    public void queueRoleResponse(final String roleId, final int timeOut) {
        try {
            final var roleObj = new RoleDTO();
            if (roleId != null) {
                roleObj.setId(roleId);
            }
            final MockResponse response = new MockResponse()
                    .setBody(new ObjectMapper().writeValueAsString(roleObj))
                    .addHeader("Content-Type", "application/json");
            if (timeOut > 0) {
                response.throttleBody(1, timeOut, TimeUnit.SECONDS);
            }
            mockBackEnd.enqueue(response);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void queueErrorResponse(final int errorCode) {
        try {
            mockBackEnd.enqueue(new MockResponse()
                    .setResponseCode(errorCode)
                    .setBody(errorCode + " error"));
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void queueResponse(final MockResponse response) {
        try {
            mockBackEnd.enqueue(response);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
