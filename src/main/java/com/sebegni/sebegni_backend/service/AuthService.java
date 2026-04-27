package com.sebegni.sebegni_backend.service;

import com.sebegni.sebegni_backend.dto.auth.AuthRequest;
import com.sebegni.sebegni_backend.dto.auth.AuthResponse;
import com.sebegni.sebegni_backend.dto.auth.RegisterRequest;
import com.sebegni.sebegni_backend.model.DriverProfile;
import com.sebegni.sebegni_backend.model.Permission;
import com.sebegni.sebegni_backend.model.User;
import com.sebegni.sebegni_backend.repository.DriverProfileRepository;
import com.sebegni.sebegni_backend.repository.PermissionRepository;
import com.sebegni.sebegni_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Set<Permission> defaultPermissions = new HashSet<>();
        if ("CLIENT".equalsIgnoreCase(request.getUserType())) {
            defaultPermissions.add(getOrCreatePermission("REQUEST_RIDE"));
            defaultPermissions.add(getOrCreatePermission("PAY_RIDE"));
        } else if ("DRIVER".equalsIgnoreCase(request.getUserType())) {
            defaultPermissions.add(getOrCreatePermission("ACCEPT_RIDE"));
            defaultPermissions.add(getOrCreatePermission("COMPLETE_RIDE"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(request.getUserType().toUpperCase())
                .enabled(true)
                .permissions(defaultPermissions)
                .build();

        User savedUser = userRepository.save(user);

        // If it's a driver, also create the profile
        if ("DRIVER".equalsIgnoreCase(savedUser.getUserType())) {
            DriverProfile profile = DriverProfile.builder()
                    .user(savedUser)
                    .balance(BigDecimal.ZERO)
                    .isAvailable(false)
                    .ratingCount(0)
                    .averageRating(BigDecimal.valueOf(5.0))
                    .build();
            driverProfileRepository.save(profile);
        }

        String jwtToken = jwtService.generateToken(savedUser);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .username(savedUser.getUsername())
                .userType(savedUser.getUserType())
                .permissions(savedUser.getPermissions().stream().map(Permission::getName).collect(Collectors.toList()))
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        
        String jwtToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .userType(user.getUserType())
                .permissions(user.getPermissions().stream().map(Permission::getName).collect(Collectors.toList()))
                .build();
    }

    private Permission getOrCreatePermission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(Permission.builder().name(name).build()));
    }
}
