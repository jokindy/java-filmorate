package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

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
    public void add(FilmDTO filmDTO) {
        Film film = new Film(filmDTO);
        int directorId = film.getDirector().getId();
        Collection<Film> films = getFilms();
        if (!films.contains(film)) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
            int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
            filmDTO.setId(filmId);
            handleGenres(film, filmId);
        } else {
            throw new ModelAlreadyExistException("This film is already added");
        }
    }

    private void handleGenres(Film film, int filmId) {
        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO film_genres(film_id, genre_id) " +
                        "VALUES (?, ?)", filmId, genre.getId());
            }
        }
    }

    @Override
    public void put(FilmDTO filmDTO) {
        Film film = new Film(filmDTO);
        int filmId = film.getId();
        Film anotherFilm = getFilmById(filmId);
        if (film.equals(anotherFilm)) {
            throw new ModelAlreadyExistException("Film id: " + filmId + " is the same");
        }
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                        "rate = ?, MPA_ID = ?, DIRECTOR_ID = ? WHERE film_id = ?", film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(),
                film.getDirector().getId(), filmId);
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
        getFilmById(id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
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
        jdbcTemplate.update(
                "INSERT INTO EVENTS(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                        "VALUES (now(), ?, 'LIKE', 'ADD', (select LIKE_ID from USER_LIKES where FILM_ID=? and USER_ID=?))",
                userId, id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        List<Integer> userLikes = jdbcTemplate.queryForList("SELECT user_id FROM user_likes WHERE film_id = ?",
                Integer.class, id);
        if (userLikes.contains(userId)) {
            jdbcTemplate.update(
                    "INSERT INTO EVENTS(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                            "VALUES (now(), ?, 'LIKE', 'REMOVE', (select LIKE_ID from USER_LIKES where FILM_ID=? and USER_ID=?))",
                    userId, id, userId);
            jdbcTemplate.update("DELETE FROM user_likes WHERE film_id = ? AND user_id = ?", id, userId);
            jdbcTemplate.update("UPDATE films SET rate = ? WHERE film_id = ?", (getFilmById(id).getRate() - 1), id);
        } else {
            throw new ModelNotFoundException("Nothing to delete");
        }
    }

    @Override
    public Collection<Film> getFilmsBySearch(String query, String by) {
        query = query.toLowerCase(Locale.ROOT);
        List<Film> films = new ArrayList<>();
        if (by.equals("title")) {
            List<String> names = jdbcTemplate.queryForList("SELECT name FROM films", String.class);
            for (String name : names) {
                if (name.contains(query)) {
                    Film film = jdbcTemplate.queryForObject("SELECT * FROM films WHERE name = ?", this::mapRowToFilm,
                            name);
                    films.add(film);
                }
            }
        } else {
            // реализация для поиску по режиссеру
            return new ArrayList<>();
        }
        return films;
    }

    @Override
    public Collection<Film> getSortedFilms() {
        return null;
    }


    @Override
    public Collection<Film> getPopularFilms(int count, int genreId, int year) {
        if (genreId < 0 || year < 0) {
            throw new ValidationException("Cannot be negative");
        }
        if (genreId == 0 && year == 0) {
            return jdbcTemplate.query("SELECT * FROM films ORDER BY rate DESC LIMIT ?", this::mapRowToFilm, count);
        } else if (year == 0) {
            return jdbcTemplate.query("SELECT *" +
                    "FROM films " +
                    "INNER JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID " +
                    "WHERE FILM_GENRES.GENRE_ID = ?" +
                    "ORDER BY FILMS.RATE DESC " +
                    "LIMIT ?", this::mapRowToFilm, genreId, count);
        } else if (genreId == 0) {
            return jdbcTemplate.query("select * " +
                    "from FILMS " +
                    "where extract(year from RELEASE_DATE) = ? " +
                    "order by RATE desc " +
                    "limit ?", this::mapRowToFilm, year, count);
        } else {
            return jdbcTemplate.query("select * " +
                    "from FILMS " +
                    "inner join FILM_GENRES on FILMS.FILM_ID = FILM_GENRES.FILM_ID " +
                    "where FILM_GENRES.GENRE_ID = ? and extract(year from FILMS.RELEASE_DATE) = ? " +
                    "order by FILMS.RATE desc " +
                    "limit ?", this::mapRowToFilm, genreId, year, count);
        }
    }

    @Override
    public Collection<Film> getDirectorFilms(int directorId, String sort) {
        String sql = "SELECT * FROM films WHERE director_id = ? ORDER BY ";
        if (sort.equals("like")) {
            sql += "RATE DESC";
        } else {
            sql += "RELEASE_DATE DESC";
        }
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
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
        int directorId = filmRows.getInt("director_id");
        Director director = jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?",
                this::mapRowToDirector, directorId);
        Film film = new Film(name, description, releaseDate, duration, rate, mpa, director);
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

    private Director mapRowToDirector(ResultSet directorRows, int rowNum) throws SQLException {
        int id = directorRows.getInt("director_id");
        String name = directorRows.getString("name");
        return new Director(id, name);
    }
}