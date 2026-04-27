package com.sebegni.sebegni_backend.repository;

import com.sebegni.sebegni_backend.model.Ride;
import com.sebegni.sebegni_backend.model.RideStatus;
import com.sebegni.sebegni_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    
    List<Ride> findByClient(User client);
    
    List<Ride> findByDriver(User driver);
    
    List<Ride> findByStatus(RideStatus status);
    
    // Find active rides for a driver (should be 0 or 1 based on business rules)
    List<Ride> findByDriverAndStatusIn(User driver, List<RideStatus> statuses);
}
