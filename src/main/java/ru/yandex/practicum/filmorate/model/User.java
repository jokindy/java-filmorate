package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Builder
@EqualsAndHashCode(callSuper = false)
@Data
public class User extends AbstractModel {

    @NotNull(message = "Email cannot be null")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Field must be in E-mail format")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Login cannot be blank")
    @NotNull(message = "Login cannot be null")
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
        validate();
    }

    @Override
    public void validate() {
        if (login != null && login.contains(" ")) {
            throw new ValidationException("Login can't contains spaces");
        }
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday can't be in future.");
        }
    }
}
