package com.project.smartparking.parkinglot;

import com.project.smartparking.auth.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    
    @PostMapping("/batch-details")
    public ResponseEntity<?> getParkingLotDetailsByIds(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ParkingLotDto.ParkingLotIdsRequest request) {
        try {
            // Verify token
            String email = extractEmailFromToken(authHeader);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            Map<Long, ParkingLotDto.ParkingLotDetailsResponse> parkingLotDetails = 
                    parkingLotService.getParkingLotDetailsByIds(request.getParkingLotIds());
            return ResponseEntity.ok(parkingLotDetails);

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
    
    /**
     * Delete a parking lot by ID
     * Only admin users can delete parking lots
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParkingLot(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            // Verify token and extract email
            String email = extractEmailFromToken(authHeader);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            parkingLotService.deleteParkingLot(id, email);
            return ResponseEntity.ok("Parking lot deleted successfully");

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting parking lot: " + e.getMessage());
        }
    }
    
    /**
     * Get all bookings for a specific parking lot
     * Only admin users can see all bookings for a parking lot
     */
    @GetMapping("/{id}/bookings")
    public ResponseEntity<?> getBookingsByParkingLotId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            // Verify token and extract email
            String email = extractEmailFromToken(authHeader);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            return ResponseEntity.ok(parkingLotService.getBookingsByParkingLotId(id, email));

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving bookings: " + e.getMessage());
        }
    }
    
    /**
     * Update specific fields of a parking slot (totalSlots and isOpen only)
     * Only admin users can update parking slots
     */
    @PostMapping("/{id}/update")
    public ResponseEntity<?> updateParkingSlotPartial(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody ParkingLotDto.UpdateParkingSlotPartialRequest request) {
        try {
            // Verify token and extract email
            String email = extractEmailFromToken(authHeader);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            ParkingLotDto.ParkingLotResponse response = 
                    parkingLotService.updateParkingSlotPartial(id, request, email);
            return ResponseEntity.ok(response);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating parking slot: " + e.getMessage());
        }
    }
}
