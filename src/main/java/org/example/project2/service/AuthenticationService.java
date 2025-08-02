package org.example.project2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project2.domain.Admin;
import org.example.project2.dto.AdminDto;
import org.example.project2.dto.LoginRequest;
import org.example.project2.dto.LoginResponse;
import org.example.project2.exception.FunctionalException;
import org.example.project2.exception.EmailConflictException;
import org.example.project2.mapper.AdminMapper;
import org.example.project2.repository.AdminRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AdminMapper adminMapper;
    
    public LoginResponse login(LoginRequest request) {
        log.info("Start Service: login with email={}", request.getEmail());
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new FunctionalException("Admin not found"));
            
            String token = jwtService.generateToken(admin);
            AdminDto adminDto = adminMapper.toDto(admin);
            
            log.info("End Service: login with success for email={}", request.getEmail());
            
            return LoginResponse.builder()
                .token(token)
                .admin(adminDto)
                .build();
                
        } catch (Exception e) {
            log.error("Authentication failed for email={}: {}", request.getEmail(), e.getMessage());
            throw new FunctionalException("Invalid email or password");
        }
    }
    
    public AdminDto register(Admin admin) {
        log.info("Start Service: register with email={}", admin.getEmail());
        
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new EmailConflictException("email.conflict");
        }
        
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole(Admin.Role.ADMIN);
        
        Admin savedAdmin = adminRepository.save(admin);
        AdminDto adminDto = adminMapper.toDto(savedAdmin);
        
        log.info("End Service: register with success for email={}", admin.getEmail());
        
        return adminDto;
    }
} 