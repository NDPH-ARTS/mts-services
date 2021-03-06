package uk.ac.ox.ndph.mts.practitionerserviceclient.common;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class MockWebServerWrapper {

    private MockWebServer mockBackEnd;

    public void start() {
        try {
            this.mockBackEnd = new MockWebServer();
            this.mockBackEnd.start();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static MockWebServerWrapper newStartedInstance() {
        final MockWebServerWrapper result = new MockWebServerWrapper();
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

    public void queueResponse(final String expectedBody) {
        queueResponse(0, expectedBody);
    }

    public void queueResponse(final int delayInSeconds, final String expectedBody) {
        try {
            final MockResponse response = new MockResponse()
                    .setBody(expectedBody)
                    .addHeader("Content-Type", "application/json");
            if (delayInSeconds > 0) {
                response.setHeadersDelay(delayInSeconds, TimeUnit.SECONDS);
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

    public int getRequestCount() {
        return mockBackEnd.getRequestCount();
    }

    public RecordedRequest takeRequest() {
        try {
            return mockBackEnd.takeRequest();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
