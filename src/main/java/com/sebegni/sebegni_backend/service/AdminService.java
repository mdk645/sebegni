package com.sebegni.sebegni_backend.service;

import com.sebegni.sebegni_backend.dto.auth.RegisterRequest;
import com.sebegni.sebegni_backend.model.DriverProfile;
import com.sebegni.sebegni_backend.model.Permission;
import com.sebegni.sebegni_backend.model.User;
import com.sebegni.sebegni_backend.repository.DriverProfileRepository;
import com.sebegni.sebegni_backend.repository.PermissionRepository;
import com.sebegni.sebegni_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createDriver(RegisterRequest request) {
        Set<Permission> driverPermissions = new HashSet<>();
        driverPermissions.add(getOrCreatePermission("ACCEPT_RIDE"));
        driverPermissions.add(getOrCreatePermission("COMPLETE_RIDE"));

        User driver = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType("DRIVER")
                .enabled(true)
                .permissions(driverPermissions)
                .build();

        User savedDriver = userRepository.save(driver);

        DriverProfile profile = DriverProfile.builder()
                .user(savedDriver)
                .balance(BigDecimal.ZERO)
                .isAvailable(false)
                .build();
        driverProfileRepository.save(profile);

        return savedDriver;
    }

    @Transactional
    public User toggleAccountStatus(UUID userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    private Permission getOrCreatePermission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(Permission.builder().name(name).build()));
    }
}
