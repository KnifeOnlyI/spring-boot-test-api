package fr.koi.testapi.config;

import fr.koi.testapi.exception.RestException;
import fr.koi.testapi.model.error.RestError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Configuration class to manage responses in case of error
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle all others error
     *
     * @return A response entity contains the representation of REST error
     */
    @ExceptionHandler(value = {Throwable.class})
    protected ResponseEntity<RestError> handleOther() {
        return new ResponseEntity<>(new RestError().setKey("error.server.unknow"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle the specified managed app error
     *
     * @return A response entity contains the representation of REST error
     */
    @ExceptionHandler(value = {RestException.class})
    protected ResponseEntity<RestError> handleRestException(RestException ex) {
        return new ResponseEntity<>(new RestError().setKey(ex.getErrorKey()), ex.getStatus());
    }
}
