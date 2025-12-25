package org.homework.services;

import org.homework.api.AiService;
import org.homework.logger.Logger;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Register
public class GigaChatAiService implements AiService {

    private static final String AUTH_URL = "https://gigachat.sber.ru/api/v1/oauth"; // пример, см. доку
    private static final String CHAT_URL = "https://gigachat.sber.ru/api/v1/chat/completions";

    // считай из application.properties / BotConfig
    private static final String AUTH_KEY = System.getenv("GIGACHAT_AUTH_KEY");

    @Resolve
    private Logger logger;

    private final HttpClient client = HttpClient.newHttpClient();
    private volatile String cachedToken;
    private volatile long tokenExpiresAt = 0L;

    @Override
    public String askJavaAssistant(String question) {
        try {
            String token = getAccessToken();
            String requestBody = """
                    {
                      "model": "GigaChat-Max",
                      "messages": [
                        {
                          "role": "user",
                          "content": "Объясни это как преподаватель Java для начинающего: %s"
                        }
                      ],
                      "temperature": 0.3
                    }
                    """.formatted(escapeJson(question));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CHAT_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("GigaChat error: " + response.statusCode() + " " + response.body());
                return "Извини, сервис ИИ временно недоступен. Попробуй позже.";
            }

            // Здесь по‑хорошему нужен JSON‑парсер, сейчас — очень грубый вырез текста
            String body = response.body();
            // Найти content первого ответа (упрощённо, под реальный JSON лучше взять Jackson/Gson)
            String marker = "\"content\":\"";
            int start = body.indexOf(marker);
            if (start == -1) {
                return "Не удалось разобрать ответ ИИ.";
            }
            start += marker.length();
            int end = body.indexOf("\"", start);
            String content = body.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
            return content;

        } catch (Exception e) {
            logger.error("Ошибка при обращении к GigaChat: " + e.getMessage());
            return "Произошла ошибка при обращении к ИИ. Попробуй ещё раз позже.";
        }
    }

    private String getAccessToken() throws Exception {
        long now = System.currentTimeMillis();
        if (cachedToken != null && now < tokenExpiresAt) {
            return cachedToken;
        }

        String body = """
                {
                  "scope": "GIGACHAT_API_PERS",
                  "grant_type": "client_credentials",
                  "client_secret": "%s"
                }
                """.formatted(escapeJson(AUTH_KEY));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Не удалось получить токен GigaChat: " +
                    response.statusCode() + " " + response.body());
        }

        String json = response.body();
        // опять же, в бою лучше использовать Jackson/Gson и маппинг по полям
        String tokenMarker = "\"access_token\":\"";
        int start = json.indexOf(tokenMarker);
        if (start == -1) {
            throw new IllegalStateException("access_token не найден в ответе GigaChat");
        }
        start += tokenMarker.length();
        int end = json.indexOf("\"", start);
        String token = json.substring(start, end);

        cachedToken = token;
        tokenExpiresAt = now + 50 * 60 * 1000; // например, 50 минут
        return token;
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
