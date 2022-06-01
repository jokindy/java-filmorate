package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Friendship {

    private int idFrom;
    private int idTo;
    private boolean status;

    public Friendship(int idFrom, int idTo) {
        this.idFrom = idFrom;
        this.idTo = idTo;
        this.status = false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFrom, idTo, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return (status == that.status) &&
                ((Objects.equals(idFrom, that.idFrom) && Objects.equals(idTo, that.idTo)) ||
                        (Objects.equals(idFrom, that.idTo) && Objects.equals(idTo, that.idFrom)));
    }
}
