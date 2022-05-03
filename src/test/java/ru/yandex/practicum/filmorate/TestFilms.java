package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class TestFilms {

    public static final Film film = new Film("Batman", "Man without fear",
            LocalDate.of(2005, 10, 3), 135);

    public static final Film updatedFilm = new Film("Catman", "Man without fear",
            LocalDate.of(2005, 10, 3), 135);

    public static final Film filmWithEmptyTitle = new Film(null, "Man with honor",
            LocalDate.of(2000, 4, 15), 120);

    public static final Film filmWith200CharDescription = new Film("200 characters",
            "MA9nznts0o0rqr0rTtGBj4EXEKtCkJt7Q30dbrWmoYM0nWoBKbJz7" +
            "uvj031F7Kpa8Y6NXz9Y8qepYhCXuad7m69bpPG05NhPzRMBU8dlsZe9oL5vWApd" +
            "jkROCxCpi32yMG8Kol6gLjNcQe7RecP4FBwM434pUCzxlNQwmuLEYhBXTjF8UVHuG84eEQduBSLal6WprRhE",
            LocalDate.of(1988, 6, 12), 110);

    public static final Film filmWith201CharDescription = new Film("200 characters",
            "MA9nznts0o0rqr0rTtGBj4EXEKtCkJt7Q3p0dbrWmoYM0nWoBKbJz7" +
                    "uvj031F7Kpa8Y6NXz9Y8qepYhCXuad7m69bpPG05NhPzRMBU8dlsZe9oL5vWApd" +
                    "jkROCxCpi32yMG8Kol6gLjNcQe7RecP4FBwM434pUCzxlNQwmuLEYhBXTjF8UVHuG84eEQduBSLal6WprRhE",
            LocalDate.of(1988, 6, 12), 110);

    public static final Film filmWithNegativeDuration = new Film("Tenet", "Nolan broke the Universe",
            LocalDate.of(2020, 6, 12), -100);


}
