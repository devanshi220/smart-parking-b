package com.project.smartparking.parkinglot;

import com.project.smartparking.auth.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-lots")
@CrossOrigin
public class ParkingLotController {

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getAllParkingLots(@RequestHeader("Authorization") String authHeader) {
        try {
            // Verify token
            String email = extractEmailFromToken(authHeader);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            List<ParkingLotDto.ParkingLotResponse> parkingLots = parkingLotService.getAllParkingLots();
            return ResponseEntity.ok(parkingLots);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }
    }

    @PostMapping
    public ResponseEntity<?> createParkingLot(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ParkingLotDto.CreateParkingLotRequest request) {
        try {
            // Verify token and extract email
            String email = extractEmailFromToken(authHeader);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            ParkingLotDto.ParkingLotResponse response = parkingLotService.createParkingLot(request, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateParkingLot(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ParkingLotDto.UpdateParkingLotRequest request) {
        try {
            // Verify token and extract email
            String email = extractEmailFromToken(authHeader);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            ParkingLotDto.ParkingLotResponse response = parkingLotService.updateParkingLot(id, request, email);
            return ResponseEntity.ok(response);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }
    }

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
