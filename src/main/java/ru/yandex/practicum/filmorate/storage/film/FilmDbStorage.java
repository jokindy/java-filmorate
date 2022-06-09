package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Film film) {
        Collection<Film> films = getFilms();
        if (!films.contains(film)) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
            int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
            film.setId(filmId);
            handleGenres(film, filmId);
        } else {
            throw new ModelAlreadyExistException("This film is already added");
        }
    }

    private void handleGenres(Film film, int filmId) {
        Set<Genre> genres = film.getGenres();
        System.out.println("GENRES - " + genres);
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO film_genres(film_id, genre_id) " +
                        "VALUES (?, ?)", filmId, genre.getId());
            }
        }
    }

    @Override
    public void put(Film film) {
        int filmId = film.getId();
        Film anotherFilm = getFilmById(filmId);
        if (film.equals(anotherFilm)) {
            throw new ModelAlreadyExistException("Film id: " + filmId + " is the same");
        }
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                        "rate = ?, MPA_ID = ? WHERE film_id = ?", film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(), filmId);
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        handleGenres(film, filmId);
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM films", this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM films WHERE film_id = ?", this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(String.format("Film id: %s not found", id));
        }
    }

    @Override
    public void deleteFilmById(int id) {
        try {
            jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(String.format("User id: %s not found", id));
        }
    }

    @Override
    public void putLike(int id, int userId) {
        List<Integer> userLikes = jdbcTemplate.queryForList("SELECT user_id FROM user_likes WHERE film_id = ?",
                Integer.class, id);
        if (userLikes.contains(userId)) {
            throw new ModelAlreadyExistException("Film already liked by user");
        }
        jdbcTemplate.update("INSERT INTO user_likes(film_id, user_id) VALUES (?, ?)", id, userId);
        jdbcTemplate.update("UPDATE films SET rate = ? WHERE film_id = ?",
                (getFilmById(id).getRate() + 1), id);
    }

    @Override
    public void deleteLike(int id, int userId) {
        List<Integer> userLikes = jdbcTemplate.queryForList("SELECT user_id FROM user_likes WHERE film_id = ?",
                Integer.class, id);
        if (userLikes.contains(userId)) {
            jdbcTemplate.update("DELETE FROM user_likes WHERE film_id = ? AND user_id = ?", id, userId);
            jdbcTemplate.update("UPDATE films SET rate = ? WHERE film_id = ?", (getFilmById(id).getRate() - 1), id);
        } else {
            throw new ModelNotFoundException("Nothing to delete");
        }
    }

    @Override
    public Collection<Film> getFilmsBySearch(String query, String by) {
      /*  List<Film> foundFilms = new ArrayList<>();
        if (!query.equals("empty")) {
            List<String> names = jdbcTemplate.queryForList("SELECT name FROM films", String.class);
            for (String name : names) {
                String s = name.toLowerCase(Locale.ROOT);
                if (s.contains(query)) {
                    Film film = jdbcTemplate.queryForObject("SELECT * FROM films WHERE name = ?",
                            this::mapRowToFilm, name);
                    foundFilms.add(film);
                }
            }
        } else if (!by.equals("empty")) {
            foundFilms = jdbcTemplate.query("SELECT * FROM films WHERE name = ?", this::mapRowToFilm, by);
        }
        if (foundFilms.isEmpty()) {
            throw new ModelNotFoundException("Can't find films by these parameters");
        } else {
            return foundFilms;
        }*/
        return new ArrayList<>();
    }

    @Override
    public Collection<Film> getSortedFilms() {
        return jdbcTemplate.query("SELECT * FROM films ORDER BY rate DESC", this::mapRowToFilm);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return jdbcTemplate.query("SELECT * FROM films ORDER BY rate DESC LIMIT ?", this::mapRowToFilm, count);
    }

    @Override
    public boolean isContains(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", id);
        return filmRows.next();
    }

    private Film mapRowToFilm(ResultSet filmRows, int rowNum) throws SQLException {
        int id = filmRows.getInt("film_id");
        String name = filmRows.getString("name");
        String description = filmRows.getString("description");
        LocalDate releaseDate = filmRows.getDate("release_date").toLocalDate();
        int duration = filmRows.getInt("duration");
        int rate = filmRows.getInt("rate");
        int mpaId = filmRows.getInt("mpa_id");
        MPA mpa = new MPA(mpaId);
        Film film = new Film(name, description, releaseDate, duration, rate, mpa);
        film.setId(id);
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM film_genres WHERE film_id = ?",
                this::mapRowToGenre, id);
        if (!genres.isEmpty()) {
            film.setGenres(new LinkedHashSet<>(genres));
        } else {
            film.setGenres(null);
        }
        return film;
    }

    private Genre mapRowToGenre(ResultSet genreRows, int rowNum) throws SQLException {
        return new Genre(genreRows.getInt("genre_id"));
    }

}