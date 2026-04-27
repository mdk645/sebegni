package com.sebegni.sebegni_backend.controller;

import com.sebegni.sebegni_backend.dto.ride.RideRequestDto;
import com.sebegni.sebegni_backend.dto.ride.RideResponseDto;
import com.sebegni.sebegni_backend.dto.auth.RegisterRequest;
import com.sebegni.sebegni_backend.model.User;
import com.sebegni.sebegni_backend.service.AdminService;
import com.sebegni.sebegni_backend.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RideService rideService;
    private final AdminService adminService;

    @GetMapping("/rides")
    @PreAuthorize("hasAuthority('MANAGE_RIDE')")
    public ResponseEntity<List<RideResponseDto>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @PostMapping("/rides/create")
    @PreAuthorize("hasAuthority('MANAGE_RIDE')")
    public ResponseEntity<RideResponseDto> createRideForClient(
            @RequestBody RideRequestDto request,
            @RequestParam String clientUsername
    ) {
        return ResponseEntity.ok(rideService.createRideByAdmin(request, clientUsername));
    }

    @PostMapping("/drivers")
    @PreAuthorize("hasAuthority('CREATE_DRIVER')")
    public ResponseEntity<User> createDriver(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(adminService.createDriver(request));
    }

    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM')")
    public ResponseEntity<User> toggleUserStatus(
            @PathVariable UUID id,
            @RequestParam boolean enabled
    ) {
        return ResponseEntity.ok(adminService.toggleAccountStatus(id, enabled));
    }
    
    // Add more monitoring endpoints as needed
}
