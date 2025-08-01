package org.example.project2.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project2.domain.Admin;
import org.example.project2.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin if no admin exists
        if (adminRepository.count() == 0) {
            Admin defaultAdmin = Admin.builder()
                .email("admin@edubroadcast.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("System Administrator")
                .role(Admin.Role.ADMIN)
                .build();
            
            adminRepository.save(defaultAdmin);
            log.info("Default admin created: admin@edubroadcast.com / admin123");
        }
    }
} 