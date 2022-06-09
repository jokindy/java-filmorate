package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MPA {

    private int id;

    @EqualsAndHashCode.Exclude
    private String name;

    public MPA(int id) {
        this.id = id;
        this.name = getName(id);
    }

    public MPA() {
    }

    private String getName(int id) {
        switch (id) {
            case 1:
                return "G";
            case 2:
                return "PG";
            case 3:
                return "PG-13";
            case 4:
                return "R";
            case 5:
                return "NC-17";
            default:
                return "Unrated";
        }
    }
}
