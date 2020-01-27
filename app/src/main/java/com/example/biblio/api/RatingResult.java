package com.example.biblio.api;

public class RatingResult {
    private final int downloads;
    private final int ratings;
    private final double rating_avg;

    RatingResult(int downloads, int ratings, double rating_avg) {
        this.downloads = downloads;
        this.ratings = ratings;
        this.rating_avg = rating_avg;
    }

    public int getRatings() {
        return ratings;
    }

    public double getRatingAvg() {
        return rating_avg;
    }

    public int getDownloads() {
        return downloads;
    }

    @Override
    public String toString() {
        return "RatingResult{" +
                "downloads=" + downloads +
                ", ratings=" + ratings +
                ", rating_avg=" + rating_avg +
                '}';
    }
}
