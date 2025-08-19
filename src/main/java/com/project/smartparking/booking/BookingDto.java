package com.project.smartparking.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingDto {

    public static class CreateBookingRequest {
        @NotNull(message = "Parking lot ID is required")
        private Long parkingLotId;

        @NotBlank(message = "Owner name is required")
        private String ownerName;

        @NotBlank(message = "Mobile number is required")
        private String mobileNo;

        @NotBlank(message = "Vehicle number is required")
        private String vehicalNo;

        @NotBlank(message = "Vehicle type is required")
        private String vehicalType;

        @NotBlank(message = "Timing slot is required")
        private String timingSlot;

        public Long getParkingLotId() { return parkingLotId; }
        public void setParkingLotId(Long parkingLotId) { this.parkingLotId = parkingLotId; }

        public String getOwnerName() { return ownerName; }
        public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

        public String getMobileNo() { return mobileNo; }
        public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

        public String getVehicalNo() { return vehicalNo; }
        public void setVehicalNo(String vehicalNo) { this.vehicalNo = vehicalNo; }

        public String getVehicalType() { return vehicalType; }
        public void setVehicalType(String vehicalType) { this.vehicalType = vehicalType; }

        public String getTimingSlot() { return timingSlot; }
        public void setTimingSlot(String timingSlot) { this.timingSlot = timingSlot; }
    }

    public static class UpdateBookingStatusRequest {
        @NotBlank(message = "Status is required")
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class BookingResponse {
        private Long id;
        private Long userId;
        private Long parkingLotId;
        private String parkingLotName;
        private String parkingLotAddress;
        private String ownerName;
        private String mobileNo;
        private String vehicalNo;
        private String vehicalType;
        private String timingSlot;
        private LocalDateTime createdAt;
        private String status;

        public BookingResponse() {}

        public BookingResponse(Long id, Long userId, Long parkingLotId, String parkingLotName, 
                             String parkingLotAddress, String ownerName, String mobileNo, 
                             String vehicalNo, String vehicalType, String timingSlot, 
                             LocalDateTime createdAt, String status) {
            this.id = id;
            this.userId = userId;
            this.parkingLotId = parkingLotId;
            this.parkingLotName = parkingLotName;
            this.parkingLotAddress = parkingLotAddress;
            this.ownerName = ownerName;
            this.mobileNo = mobileNo;
            this.vehicalNo = vehicalNo;
            this.vehicalType = vehicalType;
            this.timingSlot = timingSlot;
            this.createdAt = createdAt;
            this.status = status;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getParkingLotId() { return parkingLotId; }
        public void setParkingLotId(Long parkingLotId) { this.parkingLotId = parkingLotId; }

        public String getParkingLotName() { return parkingLotName; }
        public void setParkingLotName(String parkingLotName) { this.parkingLotName = parkingLotName; }

        public String getParkingLotAddress() { return parkingLotAddress; }
        public void setParkingLotAddress(String parkingLotAddress) { this.parkingLotAddress = parkingLotAddress; }

        public String getOwnerName() { return ownerName; }
        public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

        public String getMobileNo() { return mobileNo; }
        public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

        public String getVehicalNo() { return vehicalNo; }
        public void setVehicalNo(String vehicalNo) { this.vehicalNo = vehicalNo; }

        public String getVehicalType() { return vehicalType; }
        public void setVehicalType(String vehicalType) { this.vehicalType = vehicalType; }

        public String getTimingSlot() { return timingSlot; }
        public void setTimingSlot(String timingSlot) { this.timingSlot = timingSlot; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
