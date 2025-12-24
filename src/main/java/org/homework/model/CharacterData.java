package org.homework.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CharacterData {

    private final int id;
    private final String name;
    private final String status;
    private final String species;
    private final String type;
    private final String gender;
    private final Origin origin;
    private final Location location;
    private final String image;
    private final List<String> episode;
    private final String url;
    private final String created;

    // Определения вложенных классов Origin и Location...

    // Конструктор класса с аннотацией @JsonCreator для десериализации JSON
    @JsonCreator
    public CharacterData(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("status") String status,
            @JsonProperty("species") String species,
            @JsonProperty("type") String type,
            @JsonProperty("gender") String gender,
            @JsonProperty("origin") Origin origin,
            @JsonProperty("location") Location location,
            @JsonProperty("image") String image,
            @JsonProperty("episode") List<String> episode,
            @JsonProperty("url") String url,
            @JsonProperty("created") String created) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.type = type;
        this.gender = gender;
        this.origin = origin;
        this.location = location;
        this.image = image;
        this.episode = episode;
        this.url = url;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecies() {
        return species;
    }

    public String getType() {
        return type;
    }

    public String getGender() {
        return gender;
    }

    public Origin getOrigin() {
        return origin;
    }

    public Location getLocation() {
        return location;
    }

    public String getImage() {
        return image;
    }

    public List<String> getEpisode() {
        return episode;
    }

    public String getUrl() {
        return url;
    }

    public String getCreated() {
        return created;
    }

    public static class Origin {
        private final String name;
        private final String url;

        @JsonCreator
        public Origin(
                @JsonProperty("name") String name,
                @JsonProperty("url") String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "Origin{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public static class Location {
        private final String name;
        private final String url;

        @JsonCreator
        public Location(
                @JsonProperty("name") String name,
                @JsonProperty("url") String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CharacterData{" +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", species='" + species + '\'' +
                ", type='" + type + '\'' +
                ", gender='" + gender + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}