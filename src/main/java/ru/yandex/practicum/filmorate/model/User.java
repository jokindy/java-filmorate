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
public class User {

    @NotNull(message = "Email cannot be null")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Wrong e-mail format")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Login cannot be blank")
    @NotNull(message = "Login cannot be null")
    private String login;

    private String name;

    @NotNull
    private LocalDate birthday;

    private Set<Integer> friendsId;

    private Set<Integer> likedFilmsId;

    @EqualsAndHashCode.Exclude
    private int id;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if (name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
        this.friendsId = new HashSet<>();
        this.likedFilmsId = new HashSet<>();
        validate();
    }

    public void addUserToFriend(int friendId) {
        if (friendsId.contains(friendId)) {
            throw new ModelAlreadyExistException("Users are already friends");
        }
        friendsId.add(friendId);
    }

    public void deleteUserFromFriend(int friendId) {
        if (!friendsId.contains(friendId)) {
            throw new ModelNotFoundException("Nothing to delete");
        }
        friendsId.remove(friendId);
    }

    public void addLike(int filmId) {
        if (likedFilmsId.contains(filmId)) {
            throw new ModelAlreadyExistException("Film already liked by user");
        }
        likedFilmsId.add(filmId);
    }

    public void deleteLike(int filmId) {
        if (!likedFilmsId.contains(filmId)) {
            throw new ModelNotFoundException("Nothing to delete");
        }
        likedFilmsId.remove(filmId);
    }

    private void validate() {
        if (login != null && login.contains(" ")) {
            throw new ValidationException("Login can't contains spaces");
        }
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday can't be in future.");
        }
    }
}