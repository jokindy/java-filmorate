package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Builder
@EqualsAndHashCode(callSuper = false)
@Data
public class User extends AbstractModel {

    @NotNull
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @NotNull
    @NotBlank(message = "Login may not be blank")
    private String login;

    private String name;

    @NotNull
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if (name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }

    @Override
    public boolean validate() {
        return login.contains(" ") || birthday.isAfter(LocalDate.now());
    }
}
