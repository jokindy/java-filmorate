package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@AllArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Add review");
        reviewService.addReview(review);
        return review;
    }

    @PutMapping
    public Review putReview(@Valid @RequestBody Review review) {
        log.info("Update review");
        reviewService.putReview(review);
        return review;
    }

    @DeleteMapping("{reviewId}")
    public String deleteReview(@Positive(message = "Id must be positive") @PathVariable int reviewId) {
        log.info("Delete review by id: {}", reviewId);
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("{reviewId}")
    public Review getReview(@Positive(message = "Id must be positive") @PathVariable int reviewId) {
        log.info("Get review by id: {}", reviewId);
        return reviewService.getReview(reviewId);
    }

    @GetMapping()
    public Collection<Review> getReviewsByFilmId(@Positive(message = "Count must be positive")
                                                 @RequestParam(required = false, defaultValue = "10") int count,
                                                 @RequestParam(required = false, defaultValue = "0") int filmId) {
        log.info("Get reviews by filmId id: {}", filmId);
        return reviewService.getReviewsByFilmIdAndCount(filmId, count);
    }

    @PutMapping("{reviewId}/like/{userId}")
    public String putLikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        log.info("Put like to review by reviewId: {} and userId: {}", reviewId, userId);
        return reviewService.putLike(reviewId, userId);
    }

    @PutMapping("{reviewId}/dislike/{userId}")
    public String putDislikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        log.info("Put dislike to review by reviewId: {} and userId: {}", reviewId, userId);
        return reviewService.putDislike(reviewId, userId);
    }

    @DeleteMapping("{reviewId}/like/{userId}")
    public String deleteLikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        log.info("Delete like to review by reviewId: {} and userId: {}", reviewId, userId);
        return reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("{reviewId}/dislike/{userId}")
    public String deleteDislikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        log.info("Delete dislike to review by reviewId: {} and userId: {}", reviewId, userId);
        return reviewService.deleteDislike(reviewId, userId);
    }
}