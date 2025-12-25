package org.homework.api;

import org.homework.model.BotUser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public interface CommandService {

    // Пользователи
    void registerUser(Long telegramId, String username, String firstName);

    List<BotUser> getRegisteredUsers();

    // Основные команды
    SendMessage getHeroes(String chatId);

    SendMessage getHelp(String chatId);

    SendMessage getStart(String chatId);

    SendMessage getTopic(String chatId);

    // Шпаргалки
    SendMessage getJavaOopCheatsheet(String chatId);

    SendMessage getJavaCollectionsCheatsheet(String chatId);

    SendMessage getJavaStreamCheatsheet(String chatId);

    SendMessage getJavaExceptionsCheatsheet(String chatId);

    SendMessage getJavaTips(String chatId);

    SendMessage getJavaSolidCheatsheet(String chatId);

    SendMessage getJavaOopPrinciplesCheatsheet(String chatId);

    // Квиз
    SendMessage startOopQuiz(String chatId, Long userId);

    SendMessage startCollectionsQuiz(String chatId, Long userId);

    SendMessage startStreamQuiz(String chatId, Long userId);

    SendMessage startExceptionsQuiz(String chatId, Long userId);

    SendMessage startSolidQuiz(String chatId, Long userId);

    SendMessage startOopPrinciplesQuiz(String chatId, Long userId);

    SendMessage nextRandomQuizQuestion(String chatId, Long userId);

    SendMessage handleQuizAnswer(String chatId, Long userId, String answerText);

    SendMessage getQuizScore(String chatId, Long userId);

    // Неизвестная команда
    SendMessage getUnknownCommand(String chatId);

    SendMessage askJava(String chatId, String question);

}