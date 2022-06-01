package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class MPA {

    private int id;

    @JsonIgnore
    private String name;

    public MPA(int id) {
        this.id = id;
    }

    public MPA() {
    }
}
