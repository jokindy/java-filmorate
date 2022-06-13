package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<MPA> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM MPA", this::mapRowToMpa);
    }

    public MPA getMpaById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE mpa_id = ?", this::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(String.format("MPA id: %s not found", id));
        }
    }

    private MPA mapRowToMpa(ResultSet genreRows, int rowNum) throws SQLException {
        return new MPA(genreRows.getInt("mpa_id"));
    }
}
