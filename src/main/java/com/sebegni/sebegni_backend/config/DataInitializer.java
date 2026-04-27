package com.sebegni.sebegni_backend.config;

import com.sebegni.sebegni_backend.model.Permission;
import com.sebegni.sebegni_backend.model.User;
import com.sebegni.sebegni_backend.repository.PermissionRepository;
import com.sebegni.sebegni_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Create Permissions
        List<String> permissionNames = Arrays.asList(
                "CREATE_RIDE", "REQUEST_RIDE", "PAY_RIDE", 
                "ACCEPT_RIDE", "COMPLETE_RIDE",
                "CREATE_DRIVER", "MANAGE_RIDE", "MANAGE_PAYMENT", "FOLLOW_RIDE", "PAY_DRIVER", "MANAGE_SYSTEM"
        );

        permissionNames.forEach(name -> {
            if (permissionRepository.findByName(name).isEmpty()) {
                permissionRepository.save(Permission.builder().name(name).build());
            }
        });

        // 2. Create Sample Admins with PERSONALIZED Permissions
        
        // Sidi: Can create and follow rides, but NOT pay drivers
        createAdminIfNotFound("sidi", "sidi@sebegni.com", Arrays.asList("CREATE_RIDE", "FOLLOW_RIDE"));
        
        // Mariem: Can do everything Sidi does + Pay drivers, but NOT create drivers
        createAdminIfNotFound("mariem", "mariem@sebegni.com", Arrays.asList("CREATE_RIDE", "FOLLOW_RIDE", "PAY_DRIVER"));

        // 3. Create a Full Super Admin
        createAdminIfNotFound("admin", "admin@sebegni.com", permissionNames);
    }

    private void createAdminIfNotFound(String username, String email, List<String> permissions) {
        userRepository.findByUsername(username).orElseGet(() -> {
            Set<Permission> userPermissions = permissions.stream()
                    .map(name -> permissionRepository.findByName(name).orElseThrow())
                    .collect(Collectors.toSet());

            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode("password"))
                    .userType("ADMIN")
                    .enabled(true)
                    .permissions(userPermissions)
                    .build();
            
            return userRepository.save(user);
        });
    }
}
