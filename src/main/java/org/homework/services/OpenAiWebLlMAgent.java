package org.homework.services;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.homework.config.BotConfig;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;

@Register
public class OpenAiWebLlMAgent implements WebLlMAgent {

    @Resolve
    private Logger logger;

    private final OpenAIClient client;
    private final String model;

    public OpenAiWebLlMAgent() {
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(BotConfig.getOpenAiApiKey())
                .build();
        this.model = BotConfig.getOpenAiModel();

    }

    @Override
    public String answerWithLlm(String question) {
        String systemPrompt = """
                Ты — опытный преподаватель Java и ментор для начинающего разработчика.

                Требования к ответу:
                1) Отвечай по-русски, простым и дружелюбным языком.
                2) Объясняй как на живом созвоне с джуном: короткие фразы, без академического стиля.
                3) Структура ответа:
                   - Короткое интуитивное объяснение (2–3 предложения).
                   - Список из 3–7 ключевых пунктов по теме.
                   - Небольшой пример кода на Java, если это уместно.
                4) Для кода:
                   - Используй блоки `````` с краткими комментариями.
                   - Не пиши лишний шаблон (class Main и т.п.), если без него понятно.
                5) В конце, если уместно, предложи 1–2 мини-задачи для самостоятельной практики.
                6) Если вопрос неполный или непонятный, сначала задай 1–2 уточняющих вопроса, а потом предлагай ответ.
                """;

        try {
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(ChatModel.GPT_4O_MINI)
                    .addUserMessage(systemPrompt + "\n\nОбъясни: " + question)
                    .maxTokens(700L)
                    .temperature(0.3)
                    .build();

            ChatCompletion completion = client.chat()
                    .completions()
                    .create(params);

            var choice0 = completion.choices().get(0);
            var message = choice0.message();

// content() -> Optional<String>
            java.util.Optional<String> contentOpt = message.content();
            String text = contentOpt.orElse("Пустой ответ от модели.");
            logger.info("Ответ от  OpenAI: " + text);
            return text;


        } catch (Exception e) {
            logger.error("Ошибка при вызове OpenAI: " + e.getMessage());
            return "Сейчас внешний ИИ недоступен. Попробуй ещё раз позже.";
        }
    }


}
