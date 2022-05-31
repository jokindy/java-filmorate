package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Friendship {

    private int id_from;
    private int id_to;
    private boolean status;

    public Friendship(int id_from, int id_to) {
        this.id_from = id_from;
        this.id_to = id_to;
        this.status = false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_from, id_to, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return (status == that.status) &&
                ((Objects.equals(id_from, that.id_from) && Objects.equals(id_to, that.id_to)) ||
                        (Objects.equals(id_from, that.id_to) && Objects.equals(id_to, that.id_from)));
    }
}
