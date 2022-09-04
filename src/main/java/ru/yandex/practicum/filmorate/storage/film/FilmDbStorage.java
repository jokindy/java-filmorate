package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.event.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.event.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.event.Operation.REMOVE;

@Slf4j
@AllArgsConstructor
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final GenreDbStorage genreStorage;
    private final DirectorDbStorage directorStorage;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void add(Film film) {
        Collection<Film> films = getFilms();
        if (!films.contains(film)) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
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
        if (genres != null) {
            for (Genre genre : genres) {
                jdbc.update("INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
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
        jdbc.update("UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                        "rate = ?, MPA = ?, DIRECTOR_ID = ? WHERE film_id = ?", film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().toString(),
                film.getDirector().getId(), filmId);
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        handleGenres(film, filmId);
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbc.query("SELECT * FROM films", this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(int id) {
        try {
            return jdbc.queryForObject("SELECT * FROM films WHERE film_id = ?", this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(String.format("Film id: %s not found", id));
        }
    }

    @Override
    public void deleteFilmById(int id) {
        getFilmById(id);
        jdbc.update("DELETE FROM films WHERE film_id = ?", id);
    }

    @Override
    public void putRate(int filmId, int userId, int rate) {
        List<Integer> userLikes = jdbc.queryForList("SELECT user_id FROM user_likes WHERE film_id = ?",
                Integer.class, filmId);
        if (userLikes.contains(userId)) {
            throw new ModelAlreadyExistException("Film already rated by user");
        }
        jdbc.update("INSERT INTO user_likes(film_id, user_id, rate) VALUES (?, ?, ?)", filmId, userId, rate);
        updateRate(filmId);
        int likeId = getLikeId(userId, filmId);
        Event event = Event.getEvent(userId, LIKE, ADD, likeId);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void deleteRate(int filmId, int userId) {
        List<Integer> userLikes = jdbc.queryForList("SELECT user_id FROM user_likes WHERE film_id = ?",
                Integer.class, filmId);
        if (userLikes.contains(userId)) {
            jdbc.update("DELETE FROM user_likes WHERE film_id = ? AND user_id = ?", filmId, userId);
            updateRate(filmId);
            int likeId = getLikeId(userId, filmId);
            Event event = Event.getEvent(userId, LIKE, REMOVE, likeId);
            eventPublisher.publishEvent(event);
        } else {
            throw new ModelNotFoundException("Nothing to delete");
        }
    }

    @Override
    public Collection<Film> getFilmsBySearch(String query, String by) {
        query = "%" + query + "%";
        List<Film> films = new ArrayList<>();
        if ("title".equals(by)) {
            return jdbc.query("SELECT * FROM films WHERE name LIKE ?", this::mapRowToFilm, query);
        } else {
            log.info("Get list of directors similar to query: {}", query);
            List<Director> directors = directorStorage.getDirectorsByQuery(query);
            for (Director director : directors) {
                films.addAll(jdbc.query("SELECT * FROM films WHERE director_id = ?",
                        this::mapRowToFilm, director.getId()));
            }
        }
        return films;
    }

    @Override
    public Collection<Film> getSortedFilms() {
        return jdbc.query("SELECT * FROM films ORDER BY rate DESC", this::mapRowToFilm);
    }


    @Override
    public Collection<Film> getPopularFilms(int count, int genreId, int year) {
        if (genreId < 0 || year < 0) {
            throw new ValidationException("Cannot be negative");
        }
        if (genreId == 0 && year == 0) {
            log.info("Get {} popular films sorted by rate", count);
            return jdbc.query("SELECT * FROM films ORDER BY rate DESC LIMIT ?", this::mapRowToFilm, count);
        } else if (year == 0) {
            log.info("Get {} popular films by genre id: {} sorted by rate", count, genreId);
            return jdbc.query("SELECT *" +
                    "FROM films " +
                    "INNER JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID " +
                    "WHERE FILM_GENRES.GENRE_ID = ?" +
                    "ORDER BY FILMS.RATE DESC " +
                    "LIMIT ?", this::mapRowToFilm, genreId, count);
        } else if (genreId == 0) {
            log.info("Get {} popular films by year: {} sorted by rate", count, year);
            return jdbc.query("SELECT * " +
                    "from FILMS " +
                    "where extract(year from RELEASE_DATE) = ? " +
                    "order by RATE desc " +
                    "limit ?", this::mapRowToFilm, year, count);
        } else {
            log.info("Get {} popular films by genre id: {} and year: {} sorted by rate", count, genreId, year);
            return jdbc.query("select * " +
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
        return jdbc.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public boolean isContains(int id) {
        SqlRowSet filmRows = jdbc.queryForRowSet("SELECT * FROM films WHERE film_id = ?", id);
        return filmRows.next();
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return jdbc.query("SELECT f.* FROM USER_LIKES AS UL LEFT JOIN FILMS AS F ON F.FILM_ID = UL.FILM_ID" +
                        " WHERE USER_ID = ? AND UL.RATE > 5 AND UL.film_id " +
                        " IN (SELECT FILM_ID FROM USER_LIKES WHERE USER_ID = ?) ORDER BY f.RATE DESC;",
                this::mapRowToFilm, userId, friendId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("film_id");
        Film film = Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rate(rs.getDouble("rate"))
                .mpa(MPA.valueOf(rs.getString("MPA")))
                .genres(genreStorage.getListOfGenres(id))
                .build();
        int directorId = rs.getInt("director_id");
        Director director = directorStorage.getDirectorById(directorId, true);
        film.setDirector(director);
        return film;
    }

    private void updateRate(int filmId) {
        Double rate = jdbc.queryForObject("select (CAST(sum(rate) AS float) / " +
                "CAST(count(FILM_ID) AS float)) from USER_LIKES where film_id = ?", Double.class, filmId);
        rate = Objects.requireNonNullElse(rate, 0.0);
        jdbc.update("UPDATE films SET rate = ? WHERE film_id = ?", rate, filmId);
    }

    private Integer getLikeId(int userId, int filmId) {
        try {
            return jdbc.queryForObject("SELECT like_id FROM user_likes WHERE user_id = ? AND film_id = ?",
                    Integer.class, userId, filmId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
}