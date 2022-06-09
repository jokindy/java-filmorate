package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@EqualsAndHashCode
@Data
public class Film {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @EqualsAndHashCode.Exclude
    private int id;

    @NotNull
    @NotBlank(message = "Name may not be blank")
    private String name;

    @NotNull
    @Size(max = 200)
    @NotBlank(message = "Description may not be blank")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "Duration must be positive")
    private int duration;

    private int rate;

    @NotNull
    private MPA mpa;

    private LinkedHashSet<Genre> genres;

    public Film(String name, String description, LocalDate releaseDate, int duration, int rate, MPA mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
        validate();
    }

    private void validate() {
        if (releaseDate.isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Film can't be SO OLD!");
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("release_date", releaseDate);
        map.put("duration", duration);
        map.put("rate", rate);
        map.put("mpa_id", mpa.getId());
        return map;
    }
}