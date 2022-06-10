package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component("DirectorDbStorage")
public class DirectorDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(Director director) {
        String name = director.getName();
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE name = ?", name);
        if (!directorRows.isBeforeFirst()) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("directors")
                    .usingGeneratedKeyColumns("director_id");
            int id = simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue();
            director.setId(id);
        } else {
            throw new ModelAlreadyExistException("This director is already added");
        }
    }

    public void put(Director director) {
        int id = director.getId();
        Director anotherDirector = getDirectorById(id);
        if (director.equals(anotherDirector)) {
            throw new ModelAlreadyExistException(String.format("Director id: %s is the same", id));
        }
        jdbcTemplate.update("UPDATE directors SET name = ? WHERE director_id = ?", director.getName(), id);
    }

    public Director getDirectorById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?",
                    this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(String.format("Director id: %s not found", id));
        }
    }

    public void deleteDirectorById(int id) {
        getDirectorById(id);
        jdbcTemplate.update("DELETE FROM directors WHERE director_id = ?", id);
    }

    public Collection<Director> getDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors", this::mapRowToDirector);
    }

    private Director mapRowToDirector(ResultSet directorRows, int rowNum) throws SQLException {
        int id = directorRows.getInt("director_id");
        String name = directorRows.getString("name");
        return new Director(id, name);
    }

    public boolean isContains(int id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE director_id = ?", id);
        return rows.next();
    }
}
