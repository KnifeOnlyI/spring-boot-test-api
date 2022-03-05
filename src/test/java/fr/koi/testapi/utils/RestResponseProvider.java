package fr.koi.testapi.utils;

import org.springframework.http.ResponseEntity;

public interface RestResponseProvider<T> {
    ResponseEntity<T> execute();
}
