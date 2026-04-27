package com.sebegni.sebegni_backend.service;

import com.sebegni.sebegni_backend.dto.ride.RideRequestDto;
import com.sebegni.sebegni_backend.dto.ride.RideResponseDto;
import com.sebegni.sebegni_backend.model.DriverProfile;
import com.sebegni.sebegni_backend.model.Ride;
import com.sebegni.sebegni_backend.model.RideStatus;
import com.sebegni.sebegni_backend.model.User;
import com.sebegni.sebegni_backend.repository.DriverProfileRepository;
import com.sebegni.sebegni_backend.repository.RideRepository;
import com.sebegni.sebegni_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final UserRepository userRepository;
    
    private static final BigDecimal BASE_FARE = new BigDecimal("500.00");
    private static final BigDecimal PRICE_PER_KM = new BigDecimal("100.00");
    private static final double COMMISSION_RATE = 0.10;

    @Transactional
    public RideResponseDto createRide(RideRequestDto request, User client) {
        // Mock distance calculation (in a real app, this calls Google Maps Directions API)
        double distance = calculateMockDistance(request.getStartLat(), request.getStartLng(), 
                                               request.getEndLat(), request.getEndLng());
        
        BigDecimal price = BASE_FARE.add(PRICE_PER_KM.multiply(BigDecimal.valueOf(distance)));

        Ride ride = Ride.builder()
                .client(client)
                .startAddress(request.getStartAddress())
                .startLat(request.getStartLat())
                .startLng(request.getStartLng())
                .endAddress(request.getEndAddress())
                .endLat(request.getEndLat())
                .endLng(request.getEndLng())
                .distanceKm(distance)
                .durationMinutes((int) (distance * 2)) // Mock: 2 mins per km
                .price(price)
                .status(RideStatus.PENDING)
                .build();

        return mapToDto(rideRepository.save(ride));
    }

    @Transactional
    public RideResponseDto createRideByAdmin(RideRequestDto request, String clientUsername) {
        // Find existing client or return error
        User client = userRepository.findByUsername(clientUsername)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        
        return createRide(request, client);
    }

    @Transactional
    public RideResponseDto acceptRide(Long rideId, User driver) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        if (ride.getStatus() != RideStatus.PENDING) {
            throw new RuntimeException("Course already assigned or cancelled");
        }

        // Verify driver has no active courses
        List<Ride> activeRides = rideRepository.findByDriverAndStatusIn(driver, List.of(RideStatus.ASSIGNED, RideStatus.IN_PROGRESS));
        if (!activeRides.isEmpty()) {
            throw new RuntimeException("You already have an active course");
        }

        ride.setDriver(driver);
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setAssignedAt(LocalDateTime.now());
        
        return mapToDto(rideRepository.save(ride));
    }

    @Transactional
    public RideResponseDto startRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartedAt(LocalDateTime.now());
        return mapToDto(rideRepository.save(ride));
    }

    @Transactional
    public RideResponseDto completeRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        
        // Deduct commission from driver credit
        DriverProfile profile = driverProfileRepository.findByUser(ride.getDriver()).orElseThrow();
        BigDecimal commission = ride.getPrice().multiply(BigDecimal.valueOf(COMMISSION_RATE));
        profile.setBalance(profile.getBalance().subtract(commission));
        driverProfileRepository.save(profile);
        
        return mapToDto(rideRepository.save(ride));
    }

    public List<RideResponseDto> getAllRides() {
        return rideRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RideResponseDto mapToDto(Ride ride) {
        return RideResponseDto.builder()
                .id(ride.getId())
                .clientUsername(ride.getClient().getUsername())
                .driverUsername(ride.getDriver() != null ? ride.getDriver().getUsername() : null)
                .startAddress(ride.getStartAddress())
                .endAddress(ride.getEndAddress())
                .distanceKm(ride.getDistanceKm())
                .durationMinutes(ride.getDurationMinutes())
                .price(ride.getPrice())
                .status(ride.getStatus())
                .createdAt(LocalDateTime.now()) // Approx
                .build();
    }

    private double calculateMockDistance(double lat1, double lon1, double lat2, double lon2) {
        // Very basic Haversine approximation
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + 
                      Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 60 * 1.1515 * 1.609344;
    }
}
