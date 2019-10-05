package com.example.biblio.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Book {
    private String title;
    private String author;
    private String cover_image;
    private int publication_year;
    private ArrayList<String> categories;
    private Double rating;
    private String description;


    public Book(String title, String author, String cover_img, int year, ArrayList<String> categories, Double rating, String desc) {
        this.title = title;
        this.author = author;
        this.cover_image = cover_img;
        this.publication_year = year;
        this.categories = categories;
        this.rating = rating;
        this.description = desc;

    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCover_image() {
        return cover_image;
    }

    public int getPublication_year() {
        return publication_year;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public Double getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    @NonNull
    @Override
    public String toString() {
        return "["+ title + ", " + author + ", " + cover_image + ", " + publication_year + ", " + categories + ", " + rating + ", " + ((description.length() > 25) ? description.substring(0, 25).concat("...") : description) +"]";
    }
}
