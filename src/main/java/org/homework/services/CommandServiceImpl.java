package org.homework.services;

import org.homework.api.AiService;
import org.homework.api.CommandService;
import org.homework.api.DataService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;
import org.homework.model.BotUser;
import org.homework.model.QuizState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Register
public class CommandServiceImpl implements CommandService {

    @Resolve
    private AiService aiService;

    @Resolve
    private DataService dataService;

    @Resolve
    private Logger logger;

    private final Map<Long, QuizState> quizStates = new HashMap<>();

    private QuizState getOrCreateState(Long userId) {
        return quizStates.computeIfAbsent(userId, id -> new QuizState());
    }

    private static final List<String> QUIZ_TOPICS =
            List.of("oop", "collections", "stream", "exceptions", "solid", "oop_principles");

    @Override
    public SendMessage askJava(String chatId, String question) {
        String answer = aiService.askJavaAssistant(question);

        String text = """
            Твой вопрос:
            %s

            Ответ ассистента:
            %s
            """.formatted(question, answer);

        return new SendMessage(chatId, text);
    }


    // ЛОКАЛЬНОЕ хранилище пользователей (для интерфейса CommandService),
    // но getHeroes использует DataService.getRegisteredUsers()
    private final Map<Long, BotUser> users = new HashMap<>();

    @Override
    public void registerUser(Long telegramId, String username, String firstName) {
        logger.info("Регистрирую пользователя в CommandService: id=" + telegramId
                + ", firstName=" + firstName + ", username=" + username);
        users.putIfAbsent(telegramId, new BotUser(telegramId, username, firstName));
    }

    @Override
    public List<BotUser> getRegisteredUsers() {
        return new ArrayList<>(users.values());
    }

    // --------- /getHeroes как список зарегистрированных пользователей ---------

    @Override
    public SendMessage getHeroes(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        try {
            logger.info("Запрос списка пользователей для чата: " + chatId);

            List<BotUser> usersList = new ArrayList<>(users.values());
            logger.info("Найдено зарегистрированных пользователей: " + usersList.size());

            if (usersList.isEmpty()) {
                sendMessage.setText("Пока никто не зарегистрировался. Напиши /start, чтобы стать первым!");
                return sendMessage;
            }

            StringBuilder sb = new StringBuilder("👥 Зарегистрированные пользователи:\n\n");
            for (BotUser u : usersList) {
                logger.info("Пользователь: id=" + u.getTelegramId()
                        + ", firstName=" + u.getFirstName()
                        + ", username=" + u.getUsername());

                sb.append("- ");
                if (u.getFirstName() != null) {
                    sb.append(u.getFirstName());
                } else {
                    sb.append("Без имени");
                }
                if (u.getUsername() != null) {
                    sb.append(" (@").append(u.getUsername()).append(")");
                }
                sb.append("\n");
            }

            sendMessage.setText(sb.toString());
        } catch (Exception error) {
            logger.error("Ошибка при получении списка пользователей: " + error.getMessage());
            sendMessage.setText("Произошла ошибка при получении данных. Попробуйте позже.");
        }
        return sendMessage;
    }


    // --------- Базовые команды ---------

    @Override
    public SendMessage getHelp(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("""
                 Я — учебный помощник по Java.

                Доступные команды:
                /start - приветствие
                /help - это сообщение
                /topic - список тем
                /ask_java - твой вопрос по Java
                /java_oop - шпаргалка по ООП
                /java_collection - шпаргалка по коллекциям
                /getHeroes - список зарегистрированных пользователей

                Квиз:
                /quiz_oop - вопрос по ООП
                /quiz_collections - вопрос по коллекциям
                /quiz_stream - вопрос по Stream API
                /quiz_exceptions - вопрос по исключениям
                /quiz_solid - вопрос по SOLID
                /quiz_oop_principles - вопрос по принципам ООП
                /quiz_next - случайный вопрос
                /quiz_score - показать твой счёт
                """);
        return sendMessage;
    }

    @Override
    public SendMessage getStart(String chatId) {
        String text = """
                Привет! Я учебный помощник по Java 👨‍💻

                Я могу:
                /topic - показать список тем
                /java_oop - шпаргалка по ООП
                /java_collection - шпаргалка по коллекциям
                /help - полный список команд
                """;
        return new SendMessage(chatId, text);
    }

    @Override
    public SendMessage getTopic(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("""
                Выбери тему по Java:
                • ООП — /java_oop
                • Коллекции — /java_collection
                • Stream API — /java_stream
                • Исключения — /java_exceptions
                • Советы — /java_tips
                • SOLID — /java_solid
                • Принципы ООП — /java_oop_principles
                """);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/java_oop");
        row1.add("/java_collection");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("/java_stream");
        row2.add("/java_exceptions");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("/java_solid");
        row3.add("/java_oop_principles");

        keyboard.setKeyboard(List.of(row1, row2, row3));

        sendMessage.setReplyMarkup(keyboard);
        return sendMessage;
    }

    // --------- Шпаргалки ---------

    @Override
    public SendMessage getJavaOopCheatsheet(String chatId) {
        String text = """
                 🧠 Шпаргалка по ООП в Java:

                 1. Инкапсуляция — скрываем детали реализации, открываем только нужные методы.
                 2. Наследование — создаём новые классы на основе существующих (extends).
                 3. Полиморфизм — один интерфейс, разные реализации (переопределение методов).
                 4. Абстракция — выделяем только важные характеристики объектов.
                """;
        return new SendMessage(chatId, text);
    }

    @Override
    public SendMessage getJavaCollectionsCheatsheet(String chatId) {
        String text = """
                📚 Шпаргалка по коллекциям в Java:
                • List — упорядоченный список, элементы по индексу (ArrayList, LinkedList).
                • Set — уникальные элементы, без дубликатов (HashSet, TreeSet).
                • Map — пары ключ-значение (HashMap, TreeMap).
                • Queue/Deque — очереди (LinkedList, ArrayDeque).
                """;
        return new SendMessage(chatId, text);
    }

    @Override
    public SendMessage getJavaStreamCheatsheet(String chatId) {
        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                            🚰 Шпаргалка по Stream API:

                1. Создание:
                   • list.stream()
                   • Stream.of(1, 2, 3)

                2. Промежуточные операции:
                   • filter(p)  – отфильтровать элементы
                   • map(f)     – преобразовать элементы
                   • sorted()   – отсортировать
                   • distinct() – убрать дубликаты

                3. Терминальные операции:
                   • collect(Collectors.toList())
                   • forEach(...)
                   • count()
                   • anyMatch / allMatch / noneMatch

                Важно: поток можно использовать только один раз.
                            """);
        return m;
    }

    @Override
    public SendMessage getJavaExceptionsCheatsheet(String chatId) {
        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                            ⚠️ Шпаргалка по исключениям:

                1. Иерархия:
                   • Throwable
                     • Exception      – проверяемые (checked)
                     • RuntimeException – непроверяемые (unchecked)
                     • Error          – ошибки JVM

                2. Обработка:
                   try {
                       // код, который может бросить исключение
                   } catch (IOException e) {
                       // обработка
                   } finally {
                       // выполняется всегда (закрытие ресурсов)
                   }

                3. throws:
                   • public void read() throws IOException { ... }

                Совет: не глуши исключения пустыми catch и логируй важные ошибки.
                            """);
        return m;
    }

    @Override
    public SendMessage getJavaTips(String chatId) {
        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                            💡 Полезные советы по Java:

                1. Используй коллекции интерфейсов:
                   List<User> users = new ArrayList<>();

                2. Всегда переопределяй пару equals/hashCode при сравнении по полям.

                3. Не используй System.out.println в продакшене – только логгер.

                4. Предпочитай immutability: делай поля final там, где возможно.

                5. Пиши маленькие методы, каждый отвечает за одну задачу.
                            """);
        return m;
    }

    @Override
    public SendMessage getJavaSolidCheatsheet(String chatId) {
        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                🧱 Шпаргалка по SOLID:

                S — Single Responsibility
                   Класс отвечает только за одну задачу.

                O — Open/Closed
                   Классы открыты для расширения, закрыты для изменения.

                L — Liskov Substitution
                   Объекты подкласса можно использовать вместо объектов базового класса.

                I — Interface Segregation
                   Лучше несколько маленьких интерфейсов, чем один жирный.

                D — Dependency Inversion
                   Зависим от абстракций, а не от конкретных реализаций.
                """);
        return m;
    }

    @Override
    public SendMessage getJavaOopPrinciplesCheatsheet(String chatId) {
        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                🧠 Основные принципы ООП:

                Инкапсуляция
                   • Скрываем внутреннее состояние.
                   • Доступ только через методы.

                Наследование
                   • Класс может расширять другой класс (extends).
                   • Позволяет переиспользовать код.

                Полиморфизм
                   • Один интерфейс — много реализаций.

                Абстракция
                   • Выделяем главное, скрываем детали.
                """);
        return m;
    }

    // --------- Квиз: вопросы ---------

    @Override
    public SendMessage startOopQuiz(String chatId, Long userId) {
        QuizState state = getOrCreateState(userId);
        state.setCurrentTopic("oop");
        state.setWaitingForAnswer(true);

        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                            ❓ Вопрос по ООП:

                Какой принцип ООП описывается как «один интерфейс — много реализаций»?

                a) Инкапсуляция
                b) Наследование
                c) Полиморфизм

                Ответь буквой: a, b или c.
                            """);
        return m;
    }

    @Override
    public SendMessage startCollectionsQuiz(String chatId, Long userId) {
        QuizState state = getOrCreateState(userId);
        state.setCurrentTopic("collections");
        state.setWaitingForAnswer(true);

        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                            ❓ Вопрос по коллекциям:

                Какая коллекция гарантирует уникальность элементов?

                a) List
                b) Set
                c) Queue

                Ответь буквой: a, b или c.
                            """);
        return m;
    }

    @Override
    public SendMessage startStreamQuiz(String chatId, Long userId) {
        QuizState state = getOrCreateState(userId);
        state.setCurrentTopic("stream");
        state.setWaitingForAnswer(true);

        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                            ❓ Вопрос по Stream API:

                Какой метод используется для преобразования элементов потока?

                a) filter
                b) map
                c) count

                Ответь буквой: a, b или c.
                            """);
        return m;
    }

    @Override
    public SendMessage startExceptionsQuiz(String chatId, Long userId) {
        QuizState state = getOrCreateState(userId);
        state.setCurrentTopic("exceptions");
        state.setWaitingForAnswer(true);

        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                            ❓ Вопрос по исключениям:

                К какому типу относятся RuntimeException?

                a) Проверяемые (checked)
                b) Непроверяемые (unchecked)
                c) Ошибки JVM (Error)

                Ответь буквой: a, b или c.
                            """);
        return m;
    }

    @Override
    public SendMessage startSolidQuiz(String chatId, Long userId) {
        QuizState state = getOrCreateState(userId);
        state.setCurrentTopic("solid");
        state.setWaitingForAnswer(true);

        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                            ❓ Вопрос по SOLID:

                О каком принципе идёт речь: «Класс должен иметь только одну причину для изменения»?

                a) Liskov Substitution
                b) Single Responsibility
                c) Interface Segregation

                Ответь буквой: a, b или c.
                            """);
        return m;
    }

    @Override
    public SendMessage startOopPrinciplesQuiz(String chatId, Long userId) {
        QuizState state = getOrCreateState(userId);
        state.setCurrentTopic("oop_principles");
        state.setWaitingForAnswer(true);

        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("""
                            ❓ Вопрос по ООП:

                Какой принцип позволяет скрывать внутреннее состояние объекта и открывать только публичные методы?

                a) Наследование
                b) Инкапсуляция
                c) Полиморфизм

                Ответь буквой: a, b или c.
                            """);
        return m;
    }

    @Override
    public SendMessage nextRandomQuizQuestion(String chatId, Long userId) {
        String topic = QUIZ_TOPICS.get(new Random().nextInt(QUIZ_TOPICS.size()));
        return switch (topic) {
            case "oop" -> startOopQuiz(chatId, userId);
            case "collections" -> startCollectionsQuiz(chatId, userId);
            case "stream" -> startStreamQuiz(chatId, userId);
            case "exceptions" -> startExceptionsQuiz(chatId, userId);
            case "solid" -> startSolidQuiz(chatId, userId);
            case "oop_principles" -> startOopPrinciplesQuiz(chatId, userId);
            default -> getUnknownCommand(chatId);
        };
    }

    // --------- Квиз: ответ и счёт ---------

    @Override
    public SendMessage handleQuizAnswer(String chatId, Long userId, String answerText) {
        SendMessage m = new SendMessage();
        m.setChatId(chatId);

        QuizState state = quizStates.get(userId);
        if (state == null || !state.isWaitingForAnswer()) {
            m.setText("Сейчас нет активного вопроса. Начни с команды /quiz_oop, /quiz_collections, /quiz_stream, /quiz_exceptions, /quiz_solid или /quiz_oop_principles.");
            return m;
        }

        String ans = answerText.trim().toLowerCase();
        String topic = state.getCurrentTopic();
        boolean correct = false;

        switch (topic) {
            case "oop" -> correct = ans.equals("c");
            case "collections" -> correct = ans.equals("b");
            case "stream" -> correct = ans.equals("b");
            case "exceptions" -> correct = ans.equals("b");
            case "solid" -> correct = ans.equals("b");
            case "oop_principles" -> correct = ans.equals("b");
        }

        if (correct) {
            state.incrementCorrect();
            m.setText("✅ Правильно! Твой текущий счёт: " + state.getCorrectCount()
                    + "\nНапиши /quiz_next, чтобы получить следующий вопрос.");
        } else {
            m.setText("❌ Неправильно. Попробуй ещё раз или введи /quiz_next для нового вопроса.");
        }

        state.setWaitingForAnswer(false);
        state.setCurrentTopic(null);

        return m;
    }

    @Override
    public SendMessage getQuizScore(String chatId, Long userId) {
        QuizState state = quizStates.get(userId);
        int score = (state == null) ? 0 : state.getCorrectCount();

        SendMessage m = new SendMessage();
        m.setChatId(chatId);
        m.setText("🏆 Твой общий счёт правильных ответов: " + score);
        return m;
    }

    // --------- Неизвестная команда ---------

    @Override
    public SendMessage getUnknownCommand(String chatId) {
        String text = """
                Не знаю такой команды 🤔
                Посмотри список доступных: /help
                """;
        return new SendMessage(chatId, text);
    }

}