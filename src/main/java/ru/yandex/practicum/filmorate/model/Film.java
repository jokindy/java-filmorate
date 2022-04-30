package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@EqualsAndHashCode(callSuper = false)
@Data
public class Film extends AbstractModel {

    private final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @NotNull
    @NotBlank(message = "Name may not be blank")
    private String name;

    @NotNull
    @Size(max = 200)
    @NotBlank(message = "Description may not be blank")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    private int id;

    @NotNull
    @Positive(message = "Duration must be positive")
    private int duration;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        validate();
    }

    @Override
    public void validate() {
        if (releaseDate.isBefore(CINEMA_BIRTHDAY)) throw new ValidationException("Film can't be SO OLD!");
    }
}
