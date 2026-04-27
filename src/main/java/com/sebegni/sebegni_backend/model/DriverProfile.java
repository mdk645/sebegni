package com.sebegni.sebegni_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "driver_profiles")
public class DriverProfile extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean isAvailable = false;

    private Double lastLat;

    private Double lastLng;

    private LocalDateTime lastLocationUpdate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    // Photos & Documents
    private String profilePicture;
    
    private String vehiclePhoto1;
    private String vehiclePhoto2;
    private String vehiclePhoto3;
    private String vehiclePhoto4;

    private String drivingLicensePhoto;
    private String insurancePhoto;
    private String identityCardPhoto;

    private String vehicleInfo; // Modèle de voiture, Marque

    private String plateNumber;

    @Column(nullable = false)
    private Integer ratingCount = 0;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.valueOf(5.0);
}
