package org.homework.bot;

import org.homework.api.CommandService;
import org.homework.config.BotConfig;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Register
public class Bot extends TelegramLongPollingBot {

    @Resolve
    private CommandService commandService;

    @Resolve
    private Logger logger;

    @Override
    public String getBotUsername() {
        return BotConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return BotConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info("### ТЕСТОВЫЙ ЛОГ: onUpdateReceived запущен");

        logger.debug("Получено новое обновление: " + update.toString());

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            Long userId = update.getMessage().getFrom().getId();
            String username = update.getMessage().getFrom().getUserName();
            String firstName = update.getMessage().getFrom().getFirstName();

            // регистрируем пользователя
            commandService.registerUser(userId, username, firstName);

            logger.info("Обработка команды/сообщения: " + text);

            try {
                // ----- КОМАНДЫ КВИЗА -----
                if (text.equalsIgnoreCase("/quiz_oop")) {
                    execute(commandService.startOopQuiz(chatId, userId));

                } else if (text.equalsIgnoreCase("/quiz_collections")) {
                    execute(commandService.startCollectionsQuiz(chatId, userId));

                } else if (text.equalsIgnoreCase("/quiz_stream")) {
                    execute(commandService.startStreamQuiz(chatId, userId));

                } else if (text.equalsIgnoreCase("/quiz_exceptions")) {
                    execute(commandService.startExceptionsQuiz(chatId, userId));

                } else if (text.equalsIgnoreCase("/quiz_solid")) {
                    execute(commandService.startSolidQuiz(chatId, userId));

                } else if (text.equalsIgnoreCase("/quiz_oop_principles")) {
                    execute(commandService.startOopPrinciplesQuiz(chatId, userId));

                } else if (text.equalsIgnoreCase("/quiz_next")) {
                    execute(commandService.nextRandomQuizQuestion(chatId, userId));

                } else if (text.equalsIgnoreCase("/quiz_score")) {
                    execute(commandService.getQuizScore(chatId, userId));

                    // ----- СПИСОК ПОЛЬЗОВАТЕЛЕЙ -----
                } else if (text.equalsIgnoreCase("/getHeroes") || text.equalsIgnoreCase("/users")) {
                    execute(commandService.getHeroes(chatId));

                    // ----- ОСНОВНЫЕ КОМАНДЫ -----
                } else if (text.equalsIgnoreCase("/help")) {
                    execute(commandService.getHelp(chatId));

                } else if (text.equalsIgnoreCase("/start")) {
                    execute(commandService.getStart(chatId));

                } else if (text.equalsIgnoreCase("/topic")) {
                    execute(commandService.getTopic(chatId));

                } else if (text.toLowerCase().startsWith("/ask_java")) {
                    String question = text.replaceFirst("(?i)/ask_java", "").trim();
                    if (question.isEmpty()) {
                        execute(new SendMessage(chatId,
                                "Напиши так:/ask_java свой вопрос по Java"));
                    } else {
                        execute(commandService.askJava(chatId, question));

                    }
                    //???

                    // ----- ШПАРГАЛКИ -----
                } else if (text.equalsIgnoreCase("/java_oop")) {
                    execute(commandService.getJavaOopCheatsheet(chatId));

                } else if (text.equalsIgnoreCase("/java_collection")) {
                    execute(commandService.getJavaCollectionsCheatsheet(chatId));

                } else if (text.equalsIgnoreCase("/java_stream")) {
                    execute(commandService.getJavaStreamCheatsheet(chatId));

                } else if (text.equalsIgnoreCase("/java_exceptions")) {
                    execute(commandService.getJavaExceptionsCheatsheet(chatId));

                } else if (text.equalsIgnoreCase("/java_tips")) {
                    execute(commandService.getJavaTips(chatId));

                } else if (text.equalsIgnoreCase("/java_solid")) {
                    execute(commandService.getJavaSolidCheatsheet(chatId));

                } else if (text.equalsIgnoreCase("/java_oop_principles")) {
                    execute(commandService.getJavaOopPrinciplesCheatsheet(chatId));

                    // ----- ЕСЛИ НЕ КОМАНДА: ПЫТАЕМСЯ ЗАСЧИТАТЬ КАК ОТВЕТ КВИЗА -----
                } else if (!text.startsWith("/")) {
                    execute(commandService.handleQuizAnswer(chatId, userId, text));

                    // ----- НЕИЗВЕСТНАЯ КОМАНДА -----
                } else {
                    execute(commandService.getUnknownCommand(chatId));
                }

            } catch (TelegramApiException e) {
                logger.error("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
            }
        }
    }
}