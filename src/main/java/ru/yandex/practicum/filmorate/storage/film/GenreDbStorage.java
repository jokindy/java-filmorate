package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LinkedHashSet<Genre> getListOfGenres(int filmId) {
        List<Genre> genres = jdbcTemplate.query("SELECT g.GENRE_ID AS genre_id, name FROM film_genres AS f " +
                        "LEFT JOIN genres AS g ON f.GENRE_ID = g.GENRE_ID WHERE film_id = ?",
                this::mapRowToGenre, filmId);
        if (!genres.isEmpty()) {
            return new LinkedHashSet<>(genres);
        } else {
            return null;
        }
    }

    private Genre mapRowToGenre(ResultSet genreRows, int rowNum) throws SQLException {
        int genreId = genreRows.getInt("genre_id");
        Genre.Name name = Genre.Name.getEnum(genreRows.getString("name"));
        return new Genre(genreId, name);
    }
}
