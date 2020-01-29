package com.example.biblio.api;

import org.jetbrains.annotations.NotNull;

import lrusso96.simplebiblio.core.Ebook;

public class Review {
    private int providerId;
    private int ebookId;
    private String text;
    private String reviewer;
    private int rating;

    public Review() {
    }

    public Review(@NotNull User user, @NotNull Ebook ebook, String text, int rating) {
        this.ebookId = ebook.getId();
        this.providerId = SimpleBiblioCommons.getProviderId(ebook.getProviderName());
        this.reviewer = user.getUsername();
        this.text = text;
        this.rating = rating;
    }

    public int getProviderId() {
        return providerId;
    }

    public int getEbookId() {
        return ebookId;
    }

    public String getText() {
        return text;
    }

    @NotNull
    public String getReviewer() {
        return reviewer;
    }

    public int getRating() {
        return rating;
    }
}
