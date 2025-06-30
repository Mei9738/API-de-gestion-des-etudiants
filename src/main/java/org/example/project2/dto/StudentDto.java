package org.example.project2.dto;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record StudentDto(
        UUID id,
        String fullName,
        String email,
        String phoneNumber,
        String address
) {}
