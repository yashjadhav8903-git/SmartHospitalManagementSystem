package com.example.HospitalManagement.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() throws InterruptedException {
//        Thread.sleep(100);
        return "ok";
    }

//    @GetMapping("/ping")
//    public CompletableFuture<String> ping() {
//        return CompletableFuture.supplyAsync(() -> {
//            try { Thread.sleep(100); } catch (Exception e) {}
//            return "ok";
//        });
//    }

    @GetMapping("/secure")
    public String secure() {
        return "secured";
    }
    @GetMapping("/debug")
    public String debug() {
        return "debug endpoint";
    }
}
