package org.homework.services;

import org.homework.api.DataService;
import org.homework.logger.Logger;
import org.homework.model.QuizState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.jupiter.api.Assertions.*;

class CommandServiceImplTest {

    private CommandServiceImpl commandService;
    private DataService dataService;
    private Logger logger;

    @BeforeEach
    void setUp() {
        dataService = Mockito.mock(DataService.class);
        logger = Mockito.mock(Logger.class);

        commandService = new CommandServiceImpl();

        // вручную "инжектим" зависимости
        var dataField = getField(CommandServiceImpl.class, "dataService");
        var loggerField = getField(CommandServiceImpl.class, "logger");
        setField(commandService, dataField, dataService);
        setField(commandService, loggerField, logger);
    }

    // --- вспомогательные методы для установки приватных полей без DI ---
    private static java.lang.reflect.Field getField(Class<?> cls, String name) {
        try {
            java.lang.reflect.Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setField(Object target, java.lang.reflect.Field field, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getJavaOopCheatsheet_ReturnsNonEmptyText() {
        SendMessage msg = commandService.getJavaOopCheatsheet("123");
        assertEquals("123", msg.getChatId());
        assertNotNull(msg.getText());
        assertTrue(msg.getText().contains("Шпаргалка по ООП"));
    }

    @Test
    void startOopQuiz_SetsStateAndAsksQuestion() {
        Long userId = 1L;
        SendMessage msg = commandService.startOopQuiz("123", userId);

        assertEquals("123", msg.getChatId());
        assertTrue(msg.getText().contains("Вопрос по ООП"));

        // проверяем, что состояние создалось
        java.lang.reflect.Field quizStatesField = getField(CommandServiceImpl.class, "quizStates");
        var map = (java.util.Map<Long, QuizState>) getValue(commandService, quizStatesField);
        QuizState state = map.get(userId);
        assertNotNull(state);
        assertEquals("oop", state.getCurrentTopic());
        assertTrue(state.isWaitingForAnswer());
    }

    @Test
    void handleQuizAnswer_CorrectOopAnswer_IncrementsScore() {
        Long userId = 1L;
        // сначала запускаем квиз, чтобы создать состояние
        commandService.startOopQuiz("123", userId);

        SendMessage msg = commandService.handleQuizAnswer("123", userId, "c");

        assertTrue(msg.getText().contains("Правильно"));

        java.lang.reflect.Field quizStatesField = getField(CommandServiceImpl.class, "quizStates");
        var map = (java.util.Map<Long, QuizState>) getValue(commandService, quizStatesField);
        QuizState state = map.get(userId);
        assertNotNull(state);
        assertEquals(1, state.getCorrectCount());
        assertFalse(state.isWaitingForAnswer());
        assertNull(state.getCurrentTopic());
    }

    @Test
    void handleQuizAnswer_NoActiveQuestion_ShowsMessage() {
        Long userId = 2L; // не запускали квиз

        SendMessage msg = commandService.handleQuizAnswer("123", userId, "a");

        assertTrue(msg.getText().contains("Сейчас нет активного вопроса"));
    }

    @Test
    void getQuizScore_ReturnsZeroIfNoState() {
        Long userId = 99L;
        SendMessage msg = commandService.getQuizScore("123", userId);
        assertTrue(msg.getText().contains("0"));
    }

    // --- helper для чтения приватного поля ---
    private static Object getValue(Object target, java.lang.reflect.Field field) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
