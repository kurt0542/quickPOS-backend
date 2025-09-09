package com.quickpos.quickposbackend.controller;

import com.quickpos.quickposbackend.model.User;
import com.quickpos.quickposbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping
    public ResponseEntity<?> signup(@RequestBody User user){
        try{
            User createdUser = authService.signup(user);
            return ResponseEntity.ok(createdUser);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> login(@RequestBody User user){
        try{
            User loggedInUser = authService.login(user.getUsername(), user.getPassword());
            return ResponseEntity.ok(loggedInUser);
        }catch(Exception e){
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
