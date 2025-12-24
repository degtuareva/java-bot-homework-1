package org.homework.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.homework.api.DataService;
import org.homework.api.HttpService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;
import org.homework.model.BotUser;
import org.homework.model.CharacterData;
import org.homework.model.CharacterResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Register
public class DataServiceImpl implements DataService {

    @Resolve
    private Logger logger;

    @Resolve
    private HttpService httpService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Long, BotUser> users = new HashMap<>();

    @Override
    public List<CharacterData> getCharacterData() throws IOException, URISyntaxException {
        logger.debug("Запрос данных персонажей с API");

        String url = "https://rickandmortyapi.com/api/character?page=1";
        String jsonResponse = httpService.sendGetRequest(url, Map.of());

        CharacterResponse response = objectMapper.readValue(jsonResponse, CharacterResponse.class);
        return response.results;
    }

    @Override
    public void registerUser(Long telegramId, String username, String firstName) {
        logger.info("### ТЕСТОВЫЙ ЛОГ: registerUser вызван");

        logger.info("Регистрирую пользователя: id=" + telegramId
                + ", firstName=" + firstName + ", username=" + username);
        users.putIfAbsent(telegramId, new BotUser(telegramId, username, firstName));
    }


    @Override
    public List<BotUser> getRegisteredUsers() {
        return new ArrayList<>(users.values());
    }
}