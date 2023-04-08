package ru.netology.netologydiploma.security.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.netologydiploma.security.dto.LoginRequest;
import ru.netology.netologydiploma.security.Service.LoginService;

@RestController
@AllArgsConstructor
public class LoginController {

    LoginService service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(service.login(loginRequest));
    }

    @PostMapping("/check")
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("все ок");
    }
}

