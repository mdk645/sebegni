package com.sebegni.sebegni_backend.repository;

import com.sebegni.sebegni_backend.model.DriverProfile;
import com.sebegni.sebegni_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    
    Optional<DriverProfile> findByUser(User user);
    
    @Query("SELECT d FROM DriverProfile d WHERE d.isAvailable = true AND d.lastLat IS NOT NULL AND d.lastLng IS NOT NULL")
    List<DriverProfile> findAllAvailableDrivers();

    /**
     * Finds drivers within a specific bounding box (rough circle approximation) 
     * in degrees. 1 degree is roughly 111km.
     */
    @Query("SELECT d FROM DriverProfile d WHERE d.isAvailable = true " +
           "AND d.lastLat BETWEEN :minLat AND :maxLat " +
           "AND d.lastLng BETWEEN :minLng AND :maxLng")
    List<DriverProfile> findNearbyDrivers(@Param("minLat") Double minLat, 
                                          @Param("maxLat") Double maxLat, 
                                          @Param("minLng") Double minLng, 
                                          @Param("maxLng") Double maxLng);
}
