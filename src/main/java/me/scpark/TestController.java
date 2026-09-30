package me.scpark;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestController {

    @GetMapping("/hi")
    public String hi() {
        return "hello? 'http://localhost:8080/hi' request";
    }
    @GetMapping("/test")
    public String test() {
        return "hello? 'GET http://localhost:8080/test' request";}

    @PostMapping ("/test")
    public String posttest() {
        return "hello? 'POST http://localhost:8080/post' request";}
    @PutMapping ("/test")
    public String puttest() {
        return "hello? 'PUT http://localhost:8080/test' request";}
    @DeleteMapping ("/test")
    public String delettest() {
        return "hello? 'DELETE http://localhost:8080/test' request";
    }

}
