package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode
public class Director {

    private int id;

    @EqualsAndHashCode.Exclude
    private String name;

    public Director(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Director(int id) {
        this.id = id;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        return map;
    }
}
