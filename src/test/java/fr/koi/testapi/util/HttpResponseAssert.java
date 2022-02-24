package fr.koi.testapi.util;

import fr.koi.testapi.exception.RestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Assertion for response entity
 *
 * @param <T> The response entity type
 */
public class HttpResponseAssert<T> {
    /**
     * The response entity to check
     */
    private final ResponseEntity<T> response;

    /**
     * Create a new HTTP response assert
     *
     * @param response The response entity to check
     */
    public HttpResponseAssert(ResponseEntity<T> response) {
        this.response = response;

        Assertions.assertNotNull(this.response);
        Assertions.assertNotNull(this.response.getHeaders());
    }

    /**
     * Assert the specified response provider throw a REST exception
     *
     * @param responseProvider The response provider to check
     * @param expectedStatus   The expected HTTP status
     * @param expectedErrorKey The expected error key
     */
    public static void AssertRestException(
        Executable responseProvider,
        HttpStatus expectedStatus,
        String expectedErrorKey
    ) {
        boolean noError = true;

        try {
            responseProvider.execute();
        } catch (RestException e) {
            Assertions.assertEquals(expectedStatus, e.getStatus());
            Assertions.assertEquals(expectedErrorKey, e.getErrorKey());

            noError = false;
        } catch (Throwable e) {
            throw new AssertionFailedError("Expected RestException", RestException.class, e.getClass());
        }

        if (noError) {
            throw new AssertionFailedError("Expected RestException but no exception raised");
        }
    }

    /**
     * Assert the expected status code
     *
     * @param expected The expected status code
     *
     * @return this
     */
    public HttpResponseAssert<T> assertHttpStatus(HttpStatus expected) {
        Assertions.assertEquals(expected, this.response.getStatusCode());

        return this;
    }

    /**
     * Assert the expected number of headers
     *
     * @param expected The expected number of headers
     *
     * @return this
     */
    public HttpResponseAssert<T> assertNbHeaders(int expected) {
        Assertions.assertEquals(expected, this.response.getHeaders().size());

        return this;
    }

    /**
     * Assert contains a specified header
     *
     * @param expected The expected header
     *
     * @return this
     */
    public HttpResponseAssert<T> assertHasHeader(String expected) {
        Assertions.assertNotNull(this.response.getHeaders().getFirst(expected));

        return this;
    }

    /**
     * Assert contains a specified header with specified value
     *
     * @param expectedHeader The expected header
     * @param expectedValue  The expected value
     *
     * @return this
     */
    public HttpResponseAssert<T> assertHasHeaderWithValue(String expectedHeader, String expectedValue) {
        this.assertHasHeader(expectedHeader);

        if (expectedValue != null) {
            String actual = this.response.getHeaders().getFirst(expectedValue);

            Assertions.assertEquals(expectedValue, actual);
        }

        return this;
    }

    /**
     * Get a not null body
     *
     * @return The not null body
     */
    public T getNotNullBody() {
        Assertions.assertNotNull(this.response.getBody());

        return this.response.getBody();
    }
}
