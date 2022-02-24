package fr.koi.testapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * Represent a REST exception to build to corresponding error
 */
@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true)
public class RestException extends RuntimeException {
    /**
     * The HTTP status
     */
    private final HttpStatus status;

    /**
     * The error key
     */
    private final String errorKey;
}
