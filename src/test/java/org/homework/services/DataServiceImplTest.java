package org.homework.services;

import org.homework.api.HttpService;
import org.homework.logger.Logger;
import org.homework.model.BotUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataServiceImplTest {

    private DataServiceImpl dataService;
    private Logger logger;
    private HttpService httpService;

    @BeforeEach
    void setUp() {
        logger = Mockito.mock(Logger.class);
        httpService = Mockito.mock(HttpService.class);

        dataService = new DataServiceImpl();

        setField(dataService, "logger", logger);
        setField(dataService, "httpService", httpService);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void registerUser_AddsOnlyOneInstancePerId() {
        Long id = 1L;

        dataService.registerUser(id, "user1", "Test");
        dataService.registerUser(id, "user1", "Test");
        dataService.registerUser(2L, "user2", "Other");

        List<BotUser> all = dataService.getRegisteredUsers();
        assertEquals(2, all.size());

        BotUser first = all.stream()
                .filter(u -> u.getTelegramId().equals(id))
                .findFirst()
                .orElseThrow();

        assertEquals("user1", first.getUsername());
        assertEquals("Test", first.getFirstName());
    }
}
