package org.example.project2.dto;

import lombok.Builder;
import org.example.project2.enumeration.NotificationType;

import java.util.UUID;

@Builder
public record NotificationDto(
        UUID id,
        UUID studentId,
        String message,
        NotificationType type
) {}
