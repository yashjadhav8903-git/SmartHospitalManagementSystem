package com.example.HospitalManagement.Controller;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    @Value("${instance.name}")
    private String instanceName;

    @GetMapping("/check")
    public String check() {

        log.info("Request handled by instance: {}", instanceName);
        return "Response from: " + instanceName;
    }
}
