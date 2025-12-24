package org.homework.model;

public class BotUser {
    private final Long telegramId;
    private final String username;
    private final String firstName;

    public BotUser(Long telegramId, String username, String firstName) {
        this.telegramId = telegramId;
        this.username = username;
        this.firstName = firstName;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }
}