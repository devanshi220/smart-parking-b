package com.project.smartparking.parkinglot;

import com.project.smartparking.repository.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingSlot, Long> {
    
    ParkingSlot findByName(String name);
}
