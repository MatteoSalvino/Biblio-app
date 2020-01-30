package com.example.biblio.api;

import org.jetbrains.annotations.NotNull;

public class RatingResult {
    private final int downloads;
    private final int ratings;
    private final double ratingAvg;

    RatingResult(int downloads, int ratings, double ratingAvg) {
        this.downloads = downloads;
        this.ratings = ratings;
        this.ratingAvg = ratingAvg;
    }

    public int getRatings() {
        return ratings;
    }

    public double getRatingAvg() {
        return ratingAvg;
    }

    public int getDownloads() {
        return downloads;
    }

    @NotNull
    @Override
    public String toString() {
        return "RatingResult{" +
                "downloads=" + downloads +
                ", ratings=" + ratings +
                ", rating_avg=" + ratingAvg +
                '}';
    }
}
