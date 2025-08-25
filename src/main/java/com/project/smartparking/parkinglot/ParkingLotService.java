package com.project.smartparking.parkinglot;

import com.project.smartparking.booking.BookingRepository;
import com.project.smartparking.repository.Booking;
import com.project.smartparking.repository.ParkingSlot;
import com.project.smartparking.repository.User;
import com.project.smartparking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParkingLotService {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;

    public List<ParkingLotDto.ParkingLotResponse> getAllParkingLots() {
        List<ParkingSlot> parkingSlots = parkingLotRepository.findAll();
        return parkingSlots.stream()
                .map(slot -> new ParkingLotDto.ParkingLotResponse(
                        slot.getId(),
                        slot.getName(),
                        slot.getAddress(),
                        slot.getTotalSlots(),
                        slot.getBookedSlots(),
                        slot.getIsOpen()
                ))
                .collect(Collectors.toList());
    }

    public ParkingLotDto.ParkingLotResponse createParkingLot(
            ParkingLotDto.CreateParkingLotRequest request, String userEmail) {
        
        // Check if user is admin
        if (!isUserAdmin(userEmail)) {
            throw new AccessDeniedException("Only admin users can create parking lots");
        }

        // Check if parking lot with same name already exists
        ParkingSlot existingSlot = parkingLotRepository.findByName(request.getName());
        if (existingSlot != null) {
            throw new IllegalArgumentException("Parking lot with name '" + request.getName() + "' already exists");
        }

        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setName(request.getName());
        parkingSlot.setAddress(request.getAddress());
        parkingSlot.setTotalSlots(request.getTotalSlots());
        parkingSlot.setBookedSlots(0); // Initially no bookings
        parkingSlot.setIsOpen(request.getIsOpen());

        ParkingSlot savedSlot = parkingLotRepository.save(parkingSlot);

        return new ParkingLotDto.ParkingLotResponse(
                savedSlot.getId(),
                savedSlot.getName(),
                savedSlot.getAddress(),
                savedSlot.getTotalSlots(),
                savedSlot.getBookedSlots(),
                savedSlot.getIsOpen()
        );
    }

    public ParkingLotDto.ParkingLotResponse updateParkingLot(
            Long id, ParkingLotDto.UpdateParkingLotRequest request, String userEmail) {
        
        // Check if user is admin
        if (!isUserAdmin(userEmail)) {
            throw new AccessDeniedException("Only admin users can update parking lots");
        }

        Optional<ParkingSlot> optionalSlot = parkingLotRepository.findById(id);
        if (optionalSlot.isEmpty()) {
            throw new IllegalArgumentException("Parking lot with id " + id + " not found");
        }

        ParkingSlot parkingSlot = optionalSlot.get();

        // Update only non-null fields
        if (request.getName() != null) {
            // Check if another parking lot with same name exists
            ParkingSlot existingSlot = parkingLotRepository.findByName(request.getName());
            if (existingSlot != null && !existingSlot.getId().equals(id)) {
                throw new IllegalArgumentException("Parking lot with name '" + request.getName() + "' already exists");
            }
            parkingSlot.setName(request.getName());
        }
        
        if (request.getAddress() != null) {
            parkingSlot.setAddress(request.getAddress());
        }
        
        if (request.getTotalSlots() != null) {
            // Ensure total slots is not less than booked slots
            if (request.getTotalSlots() < parkingSlot.getBookedSlots()) {
                throw new IllegalArgumentException("Total slots cannot be less than currently booked slots (" + 
                        parkingSlot.getBookedSlots() + ")");
            }
            parkingSlot.setTotalSlots(request.getTotalSlots());
        }
        
        if (request.getIsOpen() != null) {
            parkingSlot.setIsOpen(request.getIsOpen());
        }

        ParkingSlot savedSlot = parkingLotRepository.save(parkingSlot);

        return new ParkingLotDto.ParkingLotResponse(
                savedSlot.getId(),
                savedSlot.getName(),
                savedSlot.getAddress(),
                savedSlot.getTotalSlots(),
                savedSlot.getBookedSlots(),
                savedSlot.getIsOpen()
        );
    }

    private boolean isUserAdmin(String email) {
        User user = userRepository.findByEmail(email);
        return user != null && "ADMIN".equals(user.getRole());
    }
    
    public Map<Long, ParkingLotDto.ParkingLotDetailsResponse> getParkingLotDetailsByIds(List<Long> ids) {
        List<ParkingSlot> parkingSlots = parkingLotRepository.findAllById(ids);
        
        Map<Long, ParkingLotDto.ParkingLotDetailsResponse> result = parkingSlots.stream()
            .collect(Collectors.toMap(
                ParkingSlot::getId,
                slot -> new ParkingLotDto.ParkingLotDetailsResponse(
                    slot.getId(),
                    slot.getName(),
                    slot.getAddress()
                )
            ));
        
        return result;
    }
    
    /**
     * Delete a parking slot by ID
     * Only admin users can delete parking slots
     */
    public void deleteParkingLot(Long id, String userEmail) {
        // Check if user is admin
        if (!isUserAdmin(userEmail)) {
            throw new AccessDeniedException("Only admin users can delete parking lots");
        }
        
        // Check if parking lot exists
        Optional<ParkingSlot> parkingSlotOpt = parkingLotRepository.findById(id);
        if (parkingSlotOpt.isEmpty()) {
            throw new IllegalArgumentException("Parking lot with id " + id + " not found");
        }
        
        // Check if there are any active bookings
        List<Booking> activeBookings = bookingRepository.findByParkingLotId(id).stream()
                .filter(booking -> !"CANCELLED".equals(booking.getStatus()) && !"COMPLETED".equals(booking.getStatus()))
                .collect(Collectors.toList());
                
        if (!activeBookings.isEmpty()) {
            throw new IllegalStateException("Cannot delete parking lot with active bookings. " + 
                    "There are " + activeBookings.size() + " active bookings.");
        }
        
        // Delete parking lot
        parkingLotRepository.deleteById(id);
    }
    
    /**
     * Get all bookings for a specific parking lot
     */
    public List<Booking> getBookingsByParkingLotId(Long parkingLotId, String userEmail) {
        // Check if parking lot exists
        Optional<ParkingSlot> parkingSlotOpt = parkingLotRepository.findById(parkingLotId);
        if (parkingSlotOpt.isEmpty()) {
            throw new IllegalArgumentException("Parking lot with id " + parkingLotId + " not found");
        }
        
        // For security: only admin users can see all bookings for a parking lot
        if (!isUserAdmin(userEmail)) {
            throw new AccessDeniedException("Only admin users can view all bookings for a parking lot");
        }
        
        // Get all bookings for this parking lot
        return bookingRepository.findByParkingLotId(parkingLotId);
    }
    
    /**
     * Update specific fields of a parking slot
     * This method only updates totalSlots and isOpen
     */
    public ParkingLotDto.ParkingLotResponse updateParkingSlotPartial(
            Long id, 
            ParkingLotDto.UpdateParkingSlotPartialRequest request, 
            String userEmail) {
        // Check if user is admin
        if (!isUserAdmin(userEmail)) {
            throw new AccessDeniedException("Only admin users can update parking lots");
        }
        
        // Check if parking lot exists
        Optional<ParkingSlot> parkingSlotOpt = parkingLotRepository.findById(id);
        if (parkingSlotOpt.isEmpty()) {
            throw new IllegalArgumentException("Parking slot with id " + id + " not found");
        }
        
        ParkingSlot parkingSlot = parkingSlotOpt.get();
        
        // Update only the specific fields
        if (request.getTotalSlots() != null) {
            // Ensure total slots is not less than booked slots
            if (request.getTotalSlots() < parkingSlot.getBookedSlots()) {
                throw new IllegalArgumentException("Total slots cannot be less than currently booked slots (" + 
                        parkingSlot.getBookedSlots() + ")");
            }
            parkingSlot.setTotalSlots(request.getTotalSlots());
        }
        
        if (request.getIsOpen() != null) {
            parkingSlot.setIsOpen(request.getIsOpen());
        }

        ParkingSlot savedSlot = parkingLotRepository.save(parkingSlot);

        return new ParkingLotDto.ParkingLotResponse(
                savedSlot.getId(),
                savedSlot.getName(),
                savedSlot.getAddress(),
                savedSlot.getTotalSlots(),
                savedSlot.getBookedSlots(),
                savedSlot.getIsOpen()
        );
    }
}
