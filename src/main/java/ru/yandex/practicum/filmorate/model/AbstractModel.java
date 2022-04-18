package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public abstract class AbstractModel {

    int id;

    public abstract boolean validate();
}
