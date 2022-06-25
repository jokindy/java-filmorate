package ru.yandex.practicum.filmorate.model.film;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.*;

@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
public class Film {

    @EqualsAndHashCode.Exclude
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    @EqualsAndHashCode.Exclude
    private Double rate;
    private MPA.Name mpa;
    private LinkedHashSet<Genre> genres;
    private Director director;

    public Film(FilmDTO filmDTO) {
        this.id = filmDTO.getId();
        this.name = filmDTO.getName();
        this.description = filmDTO.getDescription();
        this.releaseDate = filmDTO.getReleaseDate();
        this.duration = filmDTO.getDuration();
        this.rate = filmDTO.getRate();
        this.mpa = filmDTO.getMpa();
        this.genres = filmDTO.getGenres();
        this.director = filmDTO.getDirector();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("release_date", releaseDate);
        map.put("duration", duration);
        map.put("rate", rate);
        map.put("mpa", mpa);
        map.put("director_id", director.getId());
        return map;
    }
}