package org.example.project2.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project2.domain.Admin;
import org.example.project2.dto.AdminDto;
import org.example.project2.dto.LoginRequest;
import org.example.project2.dto.LoginResponse;
import org.example.project2.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthenticationApi {
    
    private final AuthenticationService authenticationService;
    
    @Operation(
        summary = "Login",
        description = "Authenticate admin and return JWT token",
        responses = {
            @ApiResponse(
                description = "Login successful",
                responseCode = "200",
                content = @Content(
                    schema = @Schema(implementation = LoginResponse.class)
                )
            )
        }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Start Api: login with email={}", request.getEmail());
        LoginResponse response = authenticationService.login(request);
        log.info("End Api: login with success for email={}", request.getEmail());
        return ResponseEntity.ok(response);
    }
} 