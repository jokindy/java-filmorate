package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MPA {

    private int id;

    @EqualsAndHashCode.Exclude
    private String name;

    public MPA(int id, Name name) {
        this.id = id;
        this.name = name.getName();
    }

    public MPA() {
    }

    public enum Name {
        G("G"),
        PG("PG"),
        PG_13("PG-13"),
        R("R"),
        NC_17("NC-17"),
        UNRATED("UNRATED");

        private final String name;

        Name(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
