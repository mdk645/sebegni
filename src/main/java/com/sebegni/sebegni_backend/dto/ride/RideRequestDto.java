package com.sebegni.sebegni_backend.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestDto {
    private String startAddress;
    private Double startLat;
    private Double startLng;
    
    private String endAddress;
    private Double endLat;
    private Double endLng;
}
