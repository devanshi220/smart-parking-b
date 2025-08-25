package com.project.smartparking.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;  

import com.project.smartparking.repository.UserRepository;
import com.project.smartparking.repository.User;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

/**
 * Controller for handling admin-specific authentication endpoints
 */
@RestController
@RequestMapping("/auth/admin")
@CrossOrigin
public class AdminController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AdminController(UserRepository userRepository, 
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    /**
     * Register a new admin user
     * Only existing admins can create other admin users
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AdminDtos.AdminRegisterRequest req) {
        
        // Verify token and check if user is admin
        String adminEmail = extractEmailFromToken(authHeader);
        if (adminEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }
        
        // Verify the user making the request is an admin
        User adminUser = userRepository.findByEmail(adminEmail);
        if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only existing admins can create new admin accounts");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        // Create new user with ADMIN role
        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setMobileNo(req.getMobileNo());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole("ADMIN");  // Set role to ADMIN
        
        userRepository.save(user);
        
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AdminDtos.AdminTokenResponse(token, "ADMIN"));
    }
    
    /**
     * Login for admin users
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminDtos.AdminLoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail());
        
        // Check if user exists, password matches, and role is ADMIN
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
        
        if (!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Admin privileges required");
        }
        
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AdminDtos.AdminTokenResponse(token, user.getRole()));
    }
    
    /**
     * Get admin details from the JWT token
     */
    @GetMapping
    public ResponseEntity<?> getAdminDetails(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract email from token
            String email = extractEmailFromToken(authHeader);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            
            // Find user by email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }
            
            // Check if user is admin
            if (!"ADMIN".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: Admin privileges required");
            }
            
            // Create and return admin details response
            AdminDtos.AdminDetailsResponse response = new AdminDtos.AdminDetailsResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobileNo(),
                user.getRole()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching admin details: " + e.getMessage());
        }
    }
    
    /**
     * Extract email from JWT token
     */
    private String extractEmailFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        
        // Validate token
        if (!jwtService.validateToken(token, email)) {
            return null;
        }

        return email;
    }
}
