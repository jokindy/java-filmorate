package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
@Data
public class Film {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @NotNull
    @NotBlank(message = "Name may not be blank")
    private String name;

    @NotNull
    @Size(max = 200)
    @NotBlank(message = "Description may not be blank")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @EqualsAndHashCode.Exclude
    private int id;

    @NotNull
    @Positive(message = "Duration must be positive")
    private int duration;

    private Set<Integer> userLikes;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.userLikes = new HashSet<>();
        validate();
    }

    public void validate() {
        if (releaseDate.isBefore(CINEMA_BIRTHDAY)) throw new ValidationException("Film can't be SO OLD!");
    }

    public void putUserLike(int userId) {
        if (userLikes.contains(userId)) {
            throw new ModelAlreadyExistException("Film already liked by user");
        }
        userLikes.add(userId);
    }

    public int getUserLikesCount() {
        return userLikes.size();
    }

    public void deleteUserLike(int userId) {
        if (!userLikes.contains(userId)) {
            throw new ModelNotFoundException("Nothing to delete");
        }
        userLikes.remove(userId);
    }
}
