package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Genre {

    private int id;

    @EqualsAndHashCode.Exclude
    private String name;

    public Genre(int id, Name name) {
        this.id = id;
        this.name = name.getName();
    }

    public Genre() {
    }

    public enum Name {
        COMEDY("Комедия"),
        DRAMA("Драма"),
        CARTOON("Мультфильм"),
        HORROR("Ужасы"),
        THRILLER("Триллер"),
        DETECTIVE("Детектив");

        private final String name;

        Name(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Name getEnum(String value) {
            for (Name v : values()) {
                if (v.getName().equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
