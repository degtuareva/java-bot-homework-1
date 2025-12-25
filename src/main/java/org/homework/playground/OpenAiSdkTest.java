//package org.homework.playground;
//
//import com.openai.client.OpenAIClient;
//import com.openai.client.okhttp.OpenAIOkHttpClient;
//import com.openai.models.ChatModel;
//import com.openai.models.chat.completions.ChatCompletion;
//import com.openai.models.chat.completions.ChatCompletionCreateParams;
//
//public class OpenAiSdkTest {
//
//    public static void main(String[] args) {
//        OpenAIClient client = OpenAIOkHttpClient.builder()
//                .apiKey("ТВОЙ_КЛЮЧ_ПОКА_В_ЯВНОМ_ВИДЕ_ДЛЯ_ТЕСТА")
//                .build();
//
//        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
//                .model(ChatModel.GPT_4O_MINI)
//                .addUserMessage("Скажи одно предложение по-русски, что такое Java.")
//                .maxTokens(100L)
//                .build();
//
//        ChatCompletion completion = client.chat()
//                .completions()
//                .create(params);
//
//        String answer = completion.choices()
//                .get(0)
//                .message()
//                .content()
//                .orElseThrow()
//                .get(0)
//                .text();
//
//        System.out.println(answer);
//    }
//}
