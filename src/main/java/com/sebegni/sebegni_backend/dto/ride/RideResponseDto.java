package com.sebegni.sebegni_backend.dto.ride;

import com.sebegni.sebegni_backend.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideResponseDto {
    private Long id;
    private String clientUsername;
    private String driverUsername;
    private String startAddress;
    private String endAddress;
    private Double distanceKm;
    private Integer durationMinutes;
    private BigDecimal price;
    private RideStatus status;
    private LocalDateTime createdAt;
}
