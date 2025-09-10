package com.quickpos.quickposbackend.service;

import com.quickpos.quickposbackend.model.enums.Role;
import com.quickpos.quickposbackend.model.User;
import com.quickpos.quickposbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepo userRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User signup(User user) throws Exception{
        Optional<User> existingUser = userRepo.findByUsername(user.getUsername());
        if(existingUser.isPresent()){
            throw new Exception("User already exists!");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        if(user.getRole() == null){
            user.setRole(Role.CASHIER);
        }
        return userRepo.save(user);
    }

    public User login(String username, String password) throws Exception{
        Optional<User> userData = userRepo.findByUsername(username);
        if(userData.isEmpty()) throw new Exception("Invalid username or password");

        User user = userData.get();

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new Exception("Invalid username or password");
        }

        return user;
    }
}
