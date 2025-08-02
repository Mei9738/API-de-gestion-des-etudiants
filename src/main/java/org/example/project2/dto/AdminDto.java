package org.example.project2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.project2.domain.Admin;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    private UUID id;
    private String email;
    private String fullName;
    private Admin.Role role;
} 