package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genres", this::mapRowToGenre);
    }

    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genres WHERE genre_id = ?", this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(String.format("Genre id: %s not found", id));
        }
    }

    private Genre mapRowToGenre(ResultSet genreRows, int rowNum) throws SQLException {
        return new Genre(genreRows.getInt("genre_id"));
    }
}
