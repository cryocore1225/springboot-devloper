package me.scpark.springdeveloper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuizController {

    @GetMapping("/quiz")
    public ResponseEntity<String> quiz(@RequestParam(defaultValue = "100") int code) {
        switch (code) {
            case 2:
                return ResponseEntity.badRequest().body("Bad Request");
            case 100:
                return ResponseEntity.ok("100. Which annotation handles a GET request in Spring Boot?"
                        + " A. @PostMapping  B. @GetMapping  C. @PutMapping  D. @DeleteMapping");
            case 101:
                return ResponseEntity.ok("101. When a @RestController method returns an object,"
                        + " what format is it turned into by default?"
                        + " A. XML  B. JSON  C. HTML  D. Plain text");
            case 102:
                return ResponseEntity.ok("102. Which annotation turns a plain Java class into a database table?"
                        + " A. @Service  B. @Repository  C. @Entity  D. @Component");
            case 103:
                return ResponseEntity.ok("103. Which interface gives you a full set of CRUD methods for free"
                        + " in Spring Data JPA?"
                        + " A. JpaRepository  B. CrudService  C. EntityManager  D. DataSource");
            case 104:
                return ResponseEntity.ok("104. When a single-record query finds nothing,"
                        + " which status code does this project return?"
                        + " A. 200  B. 201  C. 204  D. 404");
            default:
                return ResponseEntity.ok("No question found for code " + code);
        }
    }

    @PostMapping("/quiz")
    public ResponseEntity<String> quiz2(@RequestParam int code) {
        switch (code) {
            case 1:
                return ResponseEntity.status(403).body("Forbidden");
            case 2:
                return ResponseEntity.ok("Success");
            default:
                return ResponseEntity.status(404).body("No question found for code " + code);
        }
    }
}
