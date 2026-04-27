package com.sebegni.sebegni_backend.controller;

import com.sebegni.sebegni_backend.dto.ride.RideRequestDto;
import com.sebegni.sebegni_backend.dto.ride.RideResponseDto;
import com.sebegni.sebegni_backend.model.User;
import com.sebegni.sebegni_backend.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping("/request")
    public ResponseEntity<RideResponseDto> createRide(
            @RequestBody RideRequestDto request,
            @AuthenticationPrincipal User client
    ) {
        return ResponseEntity.ok(rideService.createRide(request, client));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<RideResponseDto> acceptRide(
            @PathVariable Long id,
            @AuthenticationPrincipal User driver
    ) {
        return ResponseEntity.ok(rideService.acceptRide(id, driver));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<RideResponseDto> startRide(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.startRide(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<RideResponseDto> completeRide(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.completeRide(id));
    }
}
