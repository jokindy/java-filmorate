package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.film.*;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;
    private final DirectorDbStorage directorStorage;

    @Override
    public void add(FilmDTO filmDTO) {
        Film film = new Film(filmDTO);
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
    public void putRate(int id, int userId, int rate) {
        List<Integer> userLikes = jdbcTemplate.queryForList("SELECT user_id FROM user_likes WHERE film_id = ?",
                Integer.class, id);
        if (userLikes.contains(userId)) {
            throw new ModelAlreadyExistException("Film already rated by user");
        }
        jdbcTemplate.update("INSERT INTO user_likes(film_id, user_id, rate) VALUES (?, ?, ?)", id, userId, rate);
        jdbcTemplate.update("UPDATE films SET rate = ? WHERE film_id = ?", getRate(id), id);
        jdbcTemplate.update("INSERT INTO EVENTS(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "VALUES (now(), ?, 'LIKE', 'ADD', " +
                "(select LIKE_ID from USER_LIKES where FILM_ID = ? and USER_ID = ?))", userId, id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        List<Integer> userLikes = jdbcTemplate.queryForList("SELECT user_id FROM user_likes WHERE film_id = ?",
                Integer.class, id);
        if (userLikes.contains(userId)) {
            jdbcTemplate.update("INSERT INTO EVENTS(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID)" +
                    "VALUES (now(), ?, 'LIKE', 'REMOVE', (select LIKE_ID from USER_LIKES " +
                    "where FILM_ID = ? and USER_ID = ?))", userId, id, userId);
            jdbcTemplate.update("UPDATE films SET rate = ? WHERE film_id = ?", getRate(id), id);
            jdbcTemplate.update("DELETE FROM user_likes WHERE film_id = ? AND user_id = ?", id, userId);
        } else {
            throw new ModelNotFoundException("Nothing to delete");
        }
    }

    @Override
    public Collection<Film> getFilmsBySearch(String query, String by) {
        query = "%" + query + "%";
        List<Film> films = new ArrayList<>();
        if ("title".equals(by)) {
            return jdbcTemplate.query("SELECT * FROM films WHERE name LIKE ?", this::mapRowToFilm, query);
        } else {
            log.info("Get list of directors similar to query: {}", query);
            List<Director> directors = directorStorage.getDirectorsByQuery(query);
            for (Director director : directors) {
                films.addAll(jdbcTemplate.query("SELECT * FROM films WHERE director_id = ?",
                        this::mapRowToFilm, director.getId()));
            }
        }
        return films;
    }

    @Override
    public Collection<Film> getSortedFilms() {
        return jdbcTemplate.query("SELECT * FROM films ORDER BY rate DESC", this::mapRowToFilm);
    }


    @Override
    public Collection<Film> getPopularFilms(int count, int genreId, int year) {
        if (genreId < 0 || year < 0) {
            throw new ValidationException("Cannot be negative");
        }
        if (genreId == 0 && year == 0) {
            log.info("Get {} popular films sorted by rate", count);
            return jdbcTemplate.query("SELECT * FROM films ORDER BY rate DESC LIMIT ?", this::mapRowToFilm, count);
        } else if (year == 0) {
            log.info("Get {} popular films by genre id: {} sorted by rate", count, genreId);
            return jdbcTemplate.query("SELECT *" +
                    "FROM films " +
                    "INNER JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID " +
                    "WHERE FILM_GENRES.GENRE_ID = ?" +
                    "ORDER BY FILMS.RATE DESC " +
                    "LIMIT ?", this::mapRowToFilm, genreId, count);
        } else if (genreId == 0) {
            log.info("Get {} popular films by year: {} sorted by rate", count, year);
            return jdbcTemplate.query("SELECT * " +
                    "from FILMS " +
                    "where extract(year from RELEASE_DATE) = ? " +
                    "order by RATE desc " +
                    "limit ?", this::mapRowToFilm, year, count);
        } else {
            log.info("Get {} popular films by genre id: {} and year: {} sorted by rate", count, genreId, year);
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

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return jdbcTemplate.query("SELECT f.FILM_ID,f.name,f.DESCRIPTION,f.RELEASE_DATE,f.DURATION,f.RATE," +
                        " f.MPA_ID,f.DIRECTOR_ID FROM USER_LIKES AS UL LEFT JOIN FILMS AS F ON F.FILM_ID = UL.FILM_ID" +
                        " WHERE USER_ID = ? AND UL.film_id" +
                        " IN (SELECT FILM_ID FROM USER_LIKES WHERE USER_ID = ?) ORDER BY f.RATE DESC;",
                this::mapRowToFilm, userId, friendId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("film_id");
        Film film = Film.builder().
                id(id).
                name(rs.getString("name")).
                description(rs.getString("description")).
                releaseDate(rs.getDate("release_date").toLocalDate()).
                duration(rs.getInt("duration")).
                rate(getRate(id)).
                build();
        int mpaId = rs.getInt("mpa_id");
        MPA mpa = mpaStorage.getMpaById(mpaId);
        film.setMpa(mpa);
        int directorId = rs.getInt("director_id");
        Director director = directorStorage.getDirectorById(directorId, true);
        film.setId(id);
        LinkedHashSet<Genre> genres = genreStorage.getListOfGenres(id);
        film.setGenres(genres);
        return film;
    }

    private double getRate(int filmId) {
        Double rate = jdbcTemplate.queryForObject("select (CAST(sum(rate) AS float) / " +
                "CAST(count(FILM_ID) AS float)) from USER_LIKES where film_id = ?", Double.class, filmId);
        if (rate != null) return rate;
        else return 0.0;
    }
}