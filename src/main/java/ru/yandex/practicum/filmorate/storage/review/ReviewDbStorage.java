package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.event.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.model.event.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.event.Operation.*;

@Component
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void addReview(Review review) {
        Collection<Review> reviews = getReviews();
        if (!reviews.contains(review)) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                    .withTableName("reviews")
                    .usingGeneratedKeyColumns("review_id");
            review.setUseful(0);
            int reviewId = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();
            review.setReviewId(reviewId);
            Event event = Event.getEvent(review.getUserId(), REVIEW, ADD, reviewId);
            eventPublisher.publishEvent(event);
        } else {
            throw new ModelAlreadyExistException("This review is already added");
        }
    }

    private Collection<Review> getReviews() {
        return jdbc.query("SELECT * FROM reviews", this::mapRowToReview);
    }

    @Override
    public void putReview(Review review) {
        int reviewId = review.getReviewId();
        Review reviewInDb = getReviewById(reviewId);
        if (review.equals(reviewInDb)) {
            throw new ModelAlreadyExistException(String.format("Review with id:%d is the same", reviewId));
        }
        Integer usefulByReviewId = getUsefulByReviewId(reviewId);
        jdbc.update("UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ?" +
                        "WHERE review_id = ?", review.getContent(), review.isPositive(), review.getUserId(),
                review.getFilmId(), usefulByReviewId, review.getReviewId());
        review.setUseful(usefulByReviewId);
        Event event = Event.getEvent(review.getUserId(), REVIEW, UPDATE, reviewId);
        eventPublisher.publishEvent(event);
    }

    private Integer getUsefulByReviewId(int reviewId) {
        Integer integer = jdbc.queryForObject("SELECT SUM(useful) FROM reviews_useful WHERE review_id = ?",
                Integer.class, reviewId);
        return integer == null ? 0 : integer;
    }

    @Override
    public void deleteReview(int reviewId) {
        Review review = getReviewById(reviewId);
        jdbc.update("DELETE FROM reviews WHERE review_id = ?", reviewId);
        Event event = Event.getEvent(review.getUserId(), REVIEW, REMOVE, reviewId);
        eventPublisher.publishEvent(event);
    }

    @Override
    public Review getReviewById(int reviewId) {
        try {
            return jdbc.queryForObject("SELECT * FROM reviews WHERE review_id = ?", this::mapRowToReview, reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(String.format("Review with id: %s not found", reviewId));
        }
    }

    @Override
    public Collection<Review> getReviewsByFilmIdAndCount(int filmId, int count) {
        if (filmId == 0) {
            return jdbc.query("SELECT * FROM reviews LIMIT ?", this::mapRowToReview, count);
        } else {
            return jdbc.query("SELECT * FROM reviews WHERE film_id = ? LIMIT ?", this::mapRowToReview, filmId, count);
        }
    }

    @Override
    public void putUseful(int reviewId, int userId, int useful) {
        List<Integer> userLikes = jdbc.queryForList("SELECT user_id FROM reviews_useful WHERE review_id = ?",
                Integer.class, reviewId);
        if (userLikes.contains(userId)) {
            throw new ModelAlreadyExistException("Review already rated by user");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("reviews_useful")
                .usingGeneratedKeyColumns("useful_id");
        int usefulId = simpleJdbcInsert.executeAndReturnKey(Map.of("review_id", reviewId, "user_id",
                userId, "useful", useful)).intValue();
        updateUseful(reviewId);
        Event event = Event.getEvent(userId, REVIEW, UPDATE, reviewId);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void deleteUseful(int reviewId, int userId, int useful) {
        Integer usefulInDb;
        try {
            usefulInDb = jdbc.queryForObject("SELECT useful FROM reviews_useful WHERE review_id = ? AND user_id = ?",
                    Integer.class, reviewId, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException("Nothing to delete");
        }
        if (usefulInDb != null && usefulInDb == useful) {
            jdbc.update("DELETE FROM reviews_useful WHERE review_id = ? AND user_id = ?", reviewId, userId);
            updateUseful(reviewId);
            Event event = Event.getEvent(userId, REVIEW, REMOVE, reviewId);
            eventPublisher.publishEvent(event);
        }
    }

    @Override
    public boolean isContains(int reviewId) {
        SqlRowSet rowSet = jdbc.queryForRowSet("SELECT * FROM reviews WHERE review_id = ?", reviewId);
        return rowSet.next();
    }

    private void updateUseful(int reviewId) {
        Integer useful = jdbc.queryForObject("SELECT sum(useful) FROM reviews_useful " +
                "WHERE review_id = ?", Integer.class, reviewId);
        useful = Objects.requireNonNullElse(useful, 0);
        jdbc.update("UPDATE reviews SET useful = ? WHERE review_id = ?", useful, reviewId);
    }

    private Review mapRowToReview(ResultSet resultSet, int i) throws SQLException {
        int reviewId = resultSet.getInt("review_id");
        String content = resultSet.getString("content");
        boolean isPositive = resultSet.getBoolean("is_positive");
        int userId = resultSet.getInt("user_id");
        int filmId = resultSet.getInt("film_id");
        int useful = resultSet.getInt("useful");
        return new Review(reviewId, content, isPositive, userId, filmId, useful);
    }
}