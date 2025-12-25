package org.homework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = BotConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                throw new IllegalStateException("application.properties not found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }

    public static String getBotUsername() {
        return props.getProperty("telegram.bot.username");
    }

    public static String getBotToken() {
        return props.getProperty("telegram.bot.token");
    }

    public static String getOpenAiApiKey() {
        return props.getProperty("openai.api.key");
    }

    public static String getOpenAiModel() {
        return props.getProperty("openai.model", "gpt-4o-mini");
    }
}
