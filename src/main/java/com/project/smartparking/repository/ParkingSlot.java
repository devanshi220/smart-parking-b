package com.project.smartparking.repository;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_slot")
public class ParkingSlot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String address;

	@Column(name = "total_slots", nullable = false)
	private Integer totalSlots;

	@Column(name = "booked_slots", nullable = false)
	private Integer bookedSlots;

	@Column(name = "isopen", nullable = false)
	private Boolean isOpen;

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

	public Boolean getIsOpen() { return isOpen; }
	public void setIsOpen(Boolean open) { isOpen = open; }
}


