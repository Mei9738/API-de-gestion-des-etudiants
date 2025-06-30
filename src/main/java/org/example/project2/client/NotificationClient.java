package org.example.project2.client;

import org.example.project2.config.OpenFeignConfig;
import org.example.project2.dto.NotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-client", url = "${app.service.notification.hostname}", configuration = OpenFeignConfig.class, path = "/api/v1")
public interface NotificationClient {
    @PostMapping("/notifications")
    NotificationDto send(@RequestBody NotificationDto notificationDto);
}
