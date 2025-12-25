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
            Ты — преподаватель Java.
            Отвечай по-русски, простыми словами.
            Структура ответа:
            1) Короткое определение (1–2 предложения).
            2) 3–5 основных пунктов по теме (списком).
            3) Очень короткий пример кода на Java, если уместно.
            Не пиши ничего лишнего, ориентируйся на начинающего.
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

            return text;


        } catch (Exception e) {
            logger.error("Ошибка при вызове OpenAI: " + e.getMessage());
            return "Сейчас внешний ИИ недоступен. Попробуй ещё раз позже.";
        }
    }


}
