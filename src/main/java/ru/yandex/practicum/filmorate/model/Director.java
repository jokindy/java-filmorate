package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Director {

    private int id;
    private String name;

    public Director(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Director() {
    }
}
