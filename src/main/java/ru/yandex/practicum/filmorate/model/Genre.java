package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {

    private int id;

    private String name;

    public Genre(int id) {
        this.id = id;
        this.name = getGenreName(id);
    }

    public Genre() {
    }

    public String getGenreName(int id) {
        switch (id) {
            case 1:
                return "Комедия";
            case 2:
                return "Драма";
            case 3:
                return "Мультфильм";
            case 4:
                return "Ужасы";
            case 5:
                return "Детектив";
            default:
                return "Тесты к спринтам";
        }
    }
}
