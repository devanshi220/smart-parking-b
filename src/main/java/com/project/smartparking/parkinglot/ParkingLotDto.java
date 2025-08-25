package com.project.smartparking.parkinglot;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ParkingLotDto {

    public static class CreateParkingLotRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Address is required")
        private String address;

        @NotNull(message = "Total slots is required")
        @Min(value = 1, message = "Total slots must be at least 1")
        private Integer totalSlots;

        @NotNull(message = "Open status is required")
        private Boolean isOpen;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public Integer getTotalSlots() { return totalSlots; }
        public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }

        public Boolean getIsOpen() { return isOpen; }
        public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }
    }

    public static class UpdateParkingLotRequest {
        private String name;
        private String address;
        
        @Min(value = 1, message = "Total slots must be at least 1")
        private Integer totalSlots;
        
        private Boolean isOpen;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public Integer getTotalSlots() { return totalSlots; }
        public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }

        public Boolean getIsOpen() { return isOpen; }
        public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }
    }

    public static class ParkingLotResponse {
        private Long id;
        private String name;
        private String address;
        private Integer totalSlots;
        private Integer bookedSlots;
        private Integer availableSlots;
        private Boolean isOpen;

        public ParkingLotResponse(Long id, String name, String address, Integer totalSlots, 
                                Integer bookedSlots, Boolean isOpen) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.totalSlots = totalSlots;
            this.bookedSlots = bookedSlots;
            this.availableSlots = totalSlots - bookedSlots;
            this.isOpen = isOpen;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public Integer getTotalSlots() { return totalSlots; }
        public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }

        public Integer getBookedSlots() { return bookedSlots; }
        public void setBookedSlots(Integer bookedSlots) { this.bookedSlots = bookedSlots; }

        public Integer getAvailableSlots() { return availableSlots; }
        public void setAvailableSlots(Integer availableSlots) { this.availableSlots = availableSlots; }

        public Boolean getIsOpen() { return isOpen; }
        public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }
    }
    
    public static class ParkingLotIdsRequest {
        @NotEmpty(message = "Parking lot IDs list cannot be empty")
        private List<Long> parkingLotIds;
        
        public List<Long> getParkingLotIds() { return parkingLotIds; }
        public void setParkingLotIds(List<Long> parkingLotIds) { this.parkingLotIds = parkingLotIds; }
    }
    
    public static class ParkingLotDetailsResponse {
        private Long id;
        private String name;
        private String address;
        
        public ParkingLotDetailsResponse(Long id, String name, String address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }
    
    public static class UpdateParkingSlotPartialRequest {
        @Min(value = 1, message = "Total slots must be at least 1")
        private Integer totalSlots;
        
        private Boolean isOpen;

        public Integer getTotalSlots() { return totalSlots; }
        public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }

        public Boolean getIsOpen() { return isOpen; }
        public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }
    }
}
