package ru.yandex.practicum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.*;

@EqualsAndHashCode
@Data
@Builder
public class Film {

    @EqualsAndHashCode.Exclude
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Double rate;
    private MPA mpa;
    private LinkedHashSet<Genre> genres;
    private Director director;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Double rate, MPA mpa,
                LinkedHashSet<Genre> genres, Director director) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
        this.genres = genres;
        this.director = director;
    }

    public Film(FilmDTO filmDTO) {
        this.id = filmDTO.getId();
        this.name = filmDTO.getName();
        this.description = filmDTO.getDescription();
        this.releaseDate = filmDTO.getReleaseDate();
        this.duration = filmDTO.getDuration();
        this.rate = filmDTO.getRate();
        this.mpa = filmDTO.getMpa();
        this.genres = filmDTO.getGenres();
        this.director = filmDTO.getDirector().get(0);
    }

    public Film(int id) {
        this.id = id;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("release_date", releaseDate);
        map.put("duration", duration);
        map.put("rate", rate);
        map.put("mpa_id", mpa.getId());
        map.put("director_id", director.getId());
        return map;
    }
}