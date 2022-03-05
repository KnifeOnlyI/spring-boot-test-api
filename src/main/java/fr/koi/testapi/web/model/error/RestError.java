package fr.koi.testapi.web.model.error;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represent a REST error
 */
@Getter
@Setter
@Accessors(chain = true)
public class RestError {
    /**
     * The error key
     */
    private String key;
}
