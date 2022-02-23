package fr.koi.testapi.resources.web;

import fr.koi.testapi.resources.model.HelloWorldDTO;
import fr.koi.testapi.services.HelloWorldService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class HelloWorldResource {
    private final HelloWorldService helloWorldService;

    public HelloWorldResource(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @GetMapping("/hello-world")
    public ResponseEntity<HelloWorldDTO> helloWorld() {
        return ResponseEntity.ok(this.helloWorldService.getHelloWorld());
    }
}
