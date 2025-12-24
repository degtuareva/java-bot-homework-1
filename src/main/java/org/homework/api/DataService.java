package org.homework.api;

import org.homework.model.BotUser;
import org.homework.model.CharacterData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface DataService {

    List<CharacterData> getCharacterData() throws IOException, URISyntaxException;

    // Работа с пользователями
    void registerUser(Long telegramId, String username, String firstName);

    List<BotUser> getRegisteredUsers();
}