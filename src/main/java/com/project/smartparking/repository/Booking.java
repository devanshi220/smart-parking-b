package com.project.smartparking.repository;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "userid", nullable = false)
	private Long userId;

	@Column(name = "parking_lot_id", nullable = false)
	private Long parkingLotId;

	@Column(name = "owner_name", nullable = false)
	private String ownerName;

	@Column(name = "mobile_no", nullable = false)
	private String mobileNo;

	@Column(name = "vehical_no", nullable = false)
	private String vehicalNo;

	@Column(name = "vehical_type", nullable = false)
	private String vehicalType;

	@Column(name = "timing_slot", nullable = false)
	private String timingSlot;

	@Column(name = "status", nullable = false)
	private String status = "PENDING";

	@Column(name = "createdAt", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }

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

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


