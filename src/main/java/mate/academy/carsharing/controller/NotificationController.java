package mate.academy.carsharing.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        notificationService.sendNotification(message);
        return ResponseEntity.ok("Notification sent: " + message);
    }
}
