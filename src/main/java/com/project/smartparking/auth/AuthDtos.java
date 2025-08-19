package com.project.smartparking.auth;

import jakarta.validation.constraints.*;

public class AuthDtos {
	public static class RegisterRequest {
		@NotBlank public String firstName;
		@NotBlank public String lastName;
		@Email @NotBlank public String email;
		@NotBlank public String mobileNo;
		@NotBlank public String password;
	}

	public static class LoginRequest {
		@Email @NotBlank public String email;
		@NotBlank public String password;
	}

	public static class TokenResponse {
		public String token;
		public TokenResponse(String token) { this.token = token; }
	}
}


