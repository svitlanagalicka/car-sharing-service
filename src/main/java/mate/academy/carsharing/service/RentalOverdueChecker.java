package mate.academy.carsharing.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.repository.RentalRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalOverdueChecker {

    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void checkOverdueRentals() {
        List<Rental> overdue = rentalRepository
                .findAllByReturnDateBeforeAndActualReturnDateIsNull(LocalDate.now());

        if (overdue.isEmpty()) {
            notificationService.sendNotification("✅ No rentals overdue today!");
        } else {
            for (Rental r : overdue) {
                notificationService.sendNotification(
                        "⚠️ Rental overdue! Rental ID: " + r.getId()
                                + ", User ID: " + r.getUser().getId()
                                + ", Car ID: " + r.getCar().getId()
                                + ", Return date: " + r.getReturnDate());
            }
        }
    }
}
