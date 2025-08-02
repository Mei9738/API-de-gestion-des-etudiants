package org.example.project2.client;

import org.example.project2.config.OpenFeignConfig;
import org.example.project2.dto.NotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "notification-client", url = "${app.service.notification.hostname}", configuration = OpenFeignConfig.class, path = "/api/v1")
public interface NotificationClient {
    @PostMapping("/notifications")
    NotificationDto send(@RequestBody NotificationDto notificationDto);
    
    @DeleteMapping("/notifications/student/{studentId}")
    void deleteNotificationsByStudentId(@PathVariable UUID studentId);
}
