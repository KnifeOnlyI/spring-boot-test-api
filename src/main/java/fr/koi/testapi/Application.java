package fr.koi.testapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class of application
 */
@SpringBootApplication
public class Application {
    /**
     * Start the application
     *
     * @param args The command list args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
