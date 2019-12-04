package com.example.biblio.api;

import org.jetbrains.annotations.NotNull;

public class RatingResult {
    private final int ratings;
    private final double rating_avg;

    RatingResult(int ratings, double rating_avg) {
        this.ratings = ratings;
        this.rating_avg = rating_avg;
    }

    public int getRatings() {
        return ratings;
    }

    public double getRating_avg() {
        return rating_avg;
    }

    @NotNull
    @Override
    public String toString() {
        return "RatingResult{" +
                "ratings=" + ratings +
                ", rating_avg=" + rating_avg +
                '}';
    }
}
