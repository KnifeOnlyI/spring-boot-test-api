package fr.koi.testapi.util;

import org.springframework.http.ResponseEntity;

public interface RestResponseProvider<T> {
    ResponseEntity<T> execute();
}
