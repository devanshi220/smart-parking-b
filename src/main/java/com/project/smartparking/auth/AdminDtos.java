package com.project.smartparking.auth;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Objects for admin authentication
 */
public class AdminDtos {
    
    public static class AdminRegisterRequest {
        @NotBlank(message = "First name is required")
        private String firstName;
        
        @NotBlank(message = "Last name is required")
        private String lastName;
        
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        private String email;
        
        @NotBlank(message = "Mobile number is required")
        private String mobileNo;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        
        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getMobileNo() { return mobileNo; }
        public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class AdminLoginRequest {
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        private String email;
        
        @NotBlank(message = "Password is required")
        private String password;
        
        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class AdminTokenResponse {
        private String token;
        private String role;
        
        public AdminTokenResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }
        
        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    public static class AdminDetailsResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String mobileNo;
        private String role;
        
        public AdminDetailsResponse(Long id, String firstName, String lastName, String email, 
                                   String mobileNo, String role) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.mobileNo = mobileNo;
            this.role = role;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getMobileNo() { return mobileNo; }
        public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
