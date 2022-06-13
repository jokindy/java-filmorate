package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static ru.yandex.practicum.filmorate.TestFilms.*;
import static ru.yandex.practicum.filmorate.TestUsers.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmStorageTests {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userDbStorage;

    @Order(1)
    @Test
    public void testAddCommonFilm() {
        filmStorage.add(commonFilm1);
        Film testFilm = filmStorage.getFilmById(1);
        Assertions.assertEquals(commonFilm1, testFilm);
    }

    @Order(2)
    @Test
    public void testAddCommonFilmTwice() {
        Assertions.assertThrows(ModelAlreadyExistException.class, () -> filmStorage.add(commonFilm1));
    }

    @Order(3)
    @Test
    public void testPutSameFilm() {
        Assertions.assertThrows(ModelAlreadyExistException.class, () -> filmStorage.put(commonFilm1));
    }

    @Order(4)
    @Test
    public void testPutUpdatedFilm() {
        updatedFilm1.setId(1);
        filmStorage.put(updatedFilm1);
        Film testFilm = filmStorage.getFilmById(1);
        Assertions.assertEquals(updatedFilm1, testFilm);
    }

    @Order(5)
    @Test
    public void testPutLike() {
        userDbStorage.add(commonUser1);
        int rateBefore = commonFilm1.getRate();
        filmStorage.putLike(1, 1);
        int rateAfter = filmStorage.getFilmById(1).getRate();
        Assertions.assertEquals(rateBefore + 1, rateAfter);
    }

    @Order(6)
    @Test
    public void testDeleteLike() {
        int rateBefore = commonFilm1.getRate();
        filmStorage.deleteLike(1, 1);
        int rateAfter = filmStorage.getFilmById(1).getRate();
        Assertions.assertEquals(rateBefore, rateAfter);
    }

    @Order(7)
    @Test
    public void testGetPopularFilms() {
        filmStorage.add(commonFilm2);
        filmStorage.add(commonFilm3);
        Collection<Film> popularFilms = List.of(commonFilm2, commonFilm3, updatedFilm1);
        Assertions.assertEquals(popularFilms, filmStorage.getPopularFilms(3, 0, 0));
    }

    @Order(10)
    @Test
    public void testGetPopularFilmWithYearAndGenre() {
        LinkedHashSet<Genre> genre = new LinkedHashSet<>(Arrays.asList(new Genre(1)));
        commonFilm1.setGenres(genre);
        filmStorage.add(commonFilm1);
        filmStorage.add(commonFilm2);
        filmStorage.add(commonFilm3);
        Collection<Film> popularFilms = List.of(commonFilm1);
        Assertions.assertEquals(popularFilms, filmStorage.getPopularFilms(3, 1, 2022));
    }

    @Order(8)
    @Test
    public void testGetAllFilms() {
        Collection<Film> list = List.of(updatedFilm1, commonFilm2, commonFilm3);
        Assertions.assertEquals(list, filmStorage.getFilms());
    }

    @Order(9)
    @Test
    public void testDeleteFilm() {
        filmStorage.deleteFilmById(1);
        Assertions.assertThrows(ModelNotFoundException.class, () -> filmStorage.getFilmById(1));
    }
}
