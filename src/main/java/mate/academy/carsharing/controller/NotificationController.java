package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    @Operation(summary = "Send message",
            description = "Send message to user about payment by telegram bot")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        notificationService.sendNotification(message);
        return ResponseEntity.ok("Notification sent: " + message);
    }
}
