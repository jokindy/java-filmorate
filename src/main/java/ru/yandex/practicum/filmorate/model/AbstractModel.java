package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public abstract class AbstractModel {

    private int id;
    public abstract void validate();
}
