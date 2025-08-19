package com.project.smartparking.booking;

import com.project.smartparking.repository.Booking;
import com.project.smartparking.repository.ParkingSlot;
import com.project.smartparking.repository.User;
import com.project.smartparking.repository.UserRepository;
import com.project.smartparking.parkinglot.ParkingLotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    public List<BookingDto.BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        
        return bookings.stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto.BookingResponse createBooking(BookingDto.CreateBookingRequest request, String userEmail) {
        // Get user
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Get parking lot
        Optional<ParkingSlot> optionalParkingLot = parkingLotRepository.findById(request.getParkingLotId());
        if (optionalParkingLot.isEmpty()) {
            throw new IllegalArgumentException("Parking lot not found");
        }

        ParkingSlot parkingLot = optionalParkingLot.get();

        // Check if parking lot is open
        if (!parkingLot.getIsOpen()) {
            throw new IllegalArgumentException("Parking lot is currently closed");
        }

        // Check if slots are available
        Long currentBookings = bookingRepository.countBookingsByParkingLotId(request.getParkingLotId());
        if (currentBookings >= parkingLot.getTotalSlots()) {
            throw new IllegalArgumentException("No available slots in this parking lot");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setParkingLotId(request.getParkingLotId());
        booking.setOwnerName(request.getOwnerName());
        booking.setMobileNo(request.getMobileNo());
        booking.setVehicalNo(request.getVehicalNo());
        booking.setVehicalType(request.getVehicalType());
        booking.setTimingSlot(request.getTimingSlot());
        booking.setStatus("PENDING"); // Default status
        booking.setCreatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        // Update booked slots count in parking lot
        parkingLot.setBookedSlots(parkingLot.getBookedSlots() + 1);
        parkingLotRepository.save(parkingLot);

        return convertToBookingResponse(savedBooking);
    }

    @Transactional
    public BookingDto.BookingResponse updateBookingStatus(Long bookingId, BookingDto.UpdateBookingStatusRequest request, String userEmail) {
        // Get user
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Get booking
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("Booking not found");
        }

        Booking booking = optionalBooking.get();

        // Check if the booking belongs to the user
        if (!booking.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only update your own bookings");
        }

        // Validate status transition
        String newStatus = request.getStatus().toUpperCase();
        if (!isValidStatusTransition(booking.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + booking.getStatus() + " to " + newStatus);
        }

        // Update booked slots count if status changes to CANCELLED
        if ("CANCELLED".equals(newStatus) && !"CANCELLED".equals(booking.getStatus())) {
            Optional<ParkingSlot> optionalParkingLot = parkingLotRepository.findById(booking.getParkingLotId());
            if (optionalParkingLot.isPresent()) {
                ParkingSlot parkingLot = optionalParkingLot.get();
                parkingLot.setBookedSlots(Math.max(0, parkingLot.getBookedSlots() - 1));
                parkingLotRepository.save(parkingLot);
            }
        }

        // Update booking status
        booking.setStatus(newStatus);
        Booking savedBooking = bookingRepository.save(booking);

        return convertToBookingResponse(savedBooking);
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case "PENDING":
                return "CONFIRMED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "CONFIRMED":
                return "COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "COMPLETED":
            case "CANCELLED":
                return false; // No transitions allowed from final states
            default:
                return false;
        }
    }

    private BookingDto.BookingResponse convertToBookingResponse(Booking booking) {
        // Get parking lot details
        Optional<ParkingSlot> optionalParkingLot = parkingLotRepository.findById(booking.getParkingLotId());
        String parkingLotName = "";
        String parkingLotAddress = "";
        
        if (optionalParkingLot.isPresent()) {
            ParkingSlot parkingLot = optionalParkingLot.get();
            parkingLotName = parkingLot.getName();
            parkingLotAddress = parkingLot.getAddress();
        }

        return new BookingDto.BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getParkingLotId(),
                parkingLotName,
                parkingLotAddress,
                booking.getOwnerName(),
                booking.getMobileNo(),
                booking.getVehicalNo(),
                booking.getVehicalType(),
                booking.getTimingSlot(),
                booking.getCreatedAt(),
                booking.getStatus()
        );
    }
}
