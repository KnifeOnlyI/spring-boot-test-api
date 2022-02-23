package fr.koi.testapi.services;

import fr.koi.testapi.domain.HelloWorld;
import fr.koi.testapi.mapper.HelloWorldMapper;
import fr.koi.testapi.repository.HelloWorldRepository;
import fr.koi.testapi.resources.model.HelloWorldDTO;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
    private final HelloWorldRepository helloWorldRepository;
    private final HelloWorldMapper helloWorldMapper;

    public HelloWorldService(HelloWorldRepository helloWorldRepository, HelloWorldMapper helloWorldMapper) {
        this.helloWorldRepository = helloWorldRepository;
        this.helloWorldMapper = helloWorldMapper;
    }

    public HelloWorldDTO getHelloWorld() {
        HelloWorld helloWorld = new HelloWorld().setMessage("Hello World !");

        this.helloWorldRepository.save(helloWorld);

        return this.helloWorldMapper.toDTO(helloWorld);
    }
}
