package com.project.smartparking.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;	

import com.project.smartparking.repository.UserRepository;
import com.project.smartparking.repository.User;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthController(UserRepository userRepository, 
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthDtos.TokenResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
        if (userRepository.existsByEmail(req.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = new User();
        user.setFirstName(req.firstName);
        user.setLastName(req.lastName);
        user.setEmail(req.email);
        user.setMobileNo(req.mobileNo);
        user.setPassword(passwordEncoder.encode(req.password)); // ✅ Encoded password
        userRepository.save(user);
        
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthDtos.TokenResponse(token));
    }
    
    // For login endpoint, you'll need to verify passwords like this:
    @PostMapping("/login")
    public ResponseEntity<AuthDtos.TokenResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        User user = userRepository.findByEmail(req.email);
        if (user == null || !passwordEncoder.matches(req.password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthDtos.TokenResponse(token));
    }
}