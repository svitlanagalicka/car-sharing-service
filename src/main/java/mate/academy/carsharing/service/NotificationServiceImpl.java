package mate.academy.carsharing.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final TelegramBot telegramBot;

    public NotificationServiceImpl() {
        this.telegramBot = new TelegramBot();
    }

    @Override
    public void sendNotification(String message) {
        try {
            telegramBot.sendMessage(chatId, message);
            System.out.println("Message was send: " + message);
        } catch (Exception e) {
            throw new RuntimeException("ERROR, can not send message: " + e.getMessage());
        }
    }

    private class TelegramBot {
        public void sendMessage(String chatId, String text)
                throws IOException, InterruptedException {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            String json = "{ \"chat_id\": \"" + chatId + "\", \"text\": \"" + text + "\" }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
