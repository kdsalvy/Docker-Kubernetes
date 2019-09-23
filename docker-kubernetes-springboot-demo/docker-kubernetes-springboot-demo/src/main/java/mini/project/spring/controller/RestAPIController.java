package mini.project.spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/rest/application")
public class RestAPIController {

    @GetMapping
    public String getResponse() {
        return "Hello from Spring Boot";
    }
}
