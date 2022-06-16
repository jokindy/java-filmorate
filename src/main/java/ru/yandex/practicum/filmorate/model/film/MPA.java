package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MPA {

    private int id;

    @EqualsAndHashCode.Exclude
    private String name;

    public MPA(int id, MPAName name) {
        this.id = id;
        this.name = name.getName();
    }

    public MPA() {
    }

    public enum MPAName {
        G("G"),
        PG("PG"),
        PG_13("PG-13"),
        R("R"),
        NC_17("NC-17");

        private final String name;

        MPAName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
