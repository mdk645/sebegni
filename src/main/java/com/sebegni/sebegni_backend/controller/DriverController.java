package com.sebegni.sebegni_backend.controller;

import com.sebegni.sebegni_backend.model.DriverProfile;
import com.sebegni.sebegni_backend.model.User;
import com.sebegni.sebegni_backend.repository.DriverProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverProfileRepository driverProfileRepository;

    @PostMapping("/location")
    public ResponseEntity<String> updateLocation(
            @AuthenticationPrincipal User user,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        DriverProfile profile = driverProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));
        
        profile.setLastLat(lat);
        profile.setLastLng(lng);
        profile.setLastLocationUpdate(LocalDateTime.now());
        driverProfileRepository.save(profile);
        
        return ResponseEntity.ok("Location updated");
    }

    @PostMapping("/availability")
    public ResponseEntity<String> toggleAvailability(
            @AuthenticationPrincipal User user,
            @RequestParam boolean available
    ) {
        DriverProfile profile = driverProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));
        
        profile.setAvailable(available);
        driverProfileRepository.save(profile);
        
        return ResponseEntity.ok("Availability updated to " + available);
    }
}
