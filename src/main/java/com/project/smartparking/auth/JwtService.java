package com.project.smartparking.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

	@Value("${app.jwt.secret:devanshiappsmartparking}")
	private String secretKey;

	@Value("${app.jwt.expMs:86400000}")
	private long expirationMs;

	private Key signingKey() {
		// Use the secret key directly as bytes, ensuring it's at least 256 bits (32 bytes) for HS256
		byte[] keyBytes = secretKey.getBytes();
		if (keyBytes.length < 32) {
			// Pad the key if it's too short
			byte[] paddedKey = new byte[32];
			System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
			keyBytes = paddedKey;
		}
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(String subject) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMs);
		return Jwts.builder()
			.setSubject(subject)
			.setIssuedAt(now)
			.setExpiration(exp)
			.signWith(signingKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(signingKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}
}


