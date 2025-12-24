package org.homework.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// Класс, представляющий полную структуру ответа API
public class CharacterResponse {
    public Info info;
    public List<CharacterData> results;

    // Класс для информации о пагинации
    public static class Info {
        public int count;
        public int pages;
        @JsonProperty("next")
        public String nextUrl;
        @JsonProperty("prev")
        public String prevUrl;
    }
}