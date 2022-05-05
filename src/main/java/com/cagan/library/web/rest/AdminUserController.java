package com.cagan.library.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    @GetMapping
    public ResponseEntity<String> createUser() {
        return ResponseEntity.ok("Created");
    }
}
