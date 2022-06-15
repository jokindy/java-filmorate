package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode
public class FilmDTO {

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

    private List<Director> director;

    public FilmDTO(String name, String description, LocalDate releaseDate, int duration, int rate, MPA mpa,
                   List<Director> director) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
        if (director == null || director.isEmpty()) {
            this.director = List.of(new Director(null));
        }
        validate();
    }

    private void validate() {
        if (releaseDate.isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Film can't be SO OLD!");
        }
    }
}
