package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Genre {

    private int id;

    @EqualsAndHashCode.Exclude
    private String name;

    public Genre(int id, GenreName name) {
        this.id = id;
        this.name = name.getName();
    }

    public Genre() {
    }

    public enum GenreName {
        COMEDY("Комедия"),
        DRAMA("Драма"),
        CARTOON("Мультфильм"),
        HORROR("Ужасы"),
        THRILLER("Триллер"),
        DETECTIVE("Детектив");

        private final String name;

        GenreName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static GenreName getEnum(String value) {
            for (GenreName v : values()) {
                if (v.getName().equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
