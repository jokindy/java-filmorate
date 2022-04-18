package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = false)
@Data
public class Film extends AbstractModel {

    @NotNull
    @NotBlank(message = "Title may not be blank")
    private String title;

    @NotNull
    @Size(max = 200)
    @NotBlank(message = "Description may not be blank")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "Duration must be positive")
    private int duration;

    public Film(String title, String description, LocalDate releaseDate, int duration) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    @Override
    public boolean validate() {
        return releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }
}
