package com.drajer.bsa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {
 
    @CrossOrigin
    @GetMapping(value = "/api/health")
    public ResponseEntity<String> Health() {
        return ResponseEntity.ok("Healthy");
    }
}
