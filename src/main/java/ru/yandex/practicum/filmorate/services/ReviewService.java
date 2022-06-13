package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Review addReview(Review review) {
        checkReviewIds(review);
        return reviewStorage.addReview(review);
    }

    public Review putReview(Review review) {
        checkReviewIds(review);
        System.out.println("REVIEW ID - " + review.getReviewId());
        return reviewStorage.putReview(review);
    }

    public String deleteReview(int reviewId) {
        reviewStorage.deleteReview(reviewId);
        return String.format("Review with id: %d was deleted", reviewId);
    }

    public Review getReview(int reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public Collection<Review> getReviewsByFilmIdAndCount(int filmId, int count) {
        return reviewStorage.getReviewsByFilmIdAndCount(filmId, count);
    }

    public String putLike(int reviewId, int userId) {
        checkIds(reviewId, userId);
        reviewStorage.putUseful(reviewId, userId, +1);
        return String.format("User with id: %d put like to review with id: %d", userId, reviewId);
    }

    public String putDislike(int reviewId, int userId) {
        checkIds(reviewId, userId);
        reviewStorage.putUseful(reviewId, userId, -1);
        return String.format("User with id: %d put dislike to review with id: %d", userId, reviewId);
    }

    public String deleteLike(int reviewId, int userId) {
        checkIds(reviewId, userId);
        reviewStorage.deleteUseful(reviewId, userId, +1);
        return String.format("User with id: %d delete like to review with id: %d", userId, reviewId);
    }

    public String deleteDislike(int reviewId, int userId) {
        checkIds(reviewId, userId);
        reviewStorage.deleteUseful(reviewId, userId, -1);
        return String.format("User with id: %d put dislike to review with id: %d", userId, reviewId);
    }

    private void checkIds(int reviewId, int userId) {
        if (!reviewStorage.isContains(reviewId)) {
            throw new ModelNotFoundException(String.format("Review with id: %d not found", reviewId));
        }
        if (!userStorage.isContains(userId)) {
            throw new ModelNotFoundException(String.format("User with id: %d not found", userId));
        }
    }

    private void checkReviewIds(Review review) {
        int filmId = review.getFilmId();
        int userId = review.getUserId();
        if (!filmStorage.isContains(filmId)) {
            throw new ModelNotFoundException(String.format("Film with id: %d not found", filmId));
        }
        if (!userStorage.isContains(userId)) {
            throw new ModelNotFoundException(String.format("User with id: %d not found", userId));
        }
    }
}