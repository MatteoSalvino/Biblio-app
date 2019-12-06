package com.example.biblio.api;

import org.jetbrains.annotations.NotNull;

import lrusso96.simplebiblio.core.Ebook;

import static com.example.biblio.api.SimpleBiblioHelper.getProviderId;

public class Review {
    private int provider_id;
    private int ebook_id;
    private String text;
    private String reviewer;
    private int rating;

    public Review() {
    }

    public Review(@NotNull User user, @NotNull Ebook ebook, String text, int rating) {
        this.ebook_id = ebook.getId();
        this.provider_id = Integer.parseInt(getProviderId(ebook.getProviderName()));
        this.reviewer = user.getUsername();
        this.text = text;
        this.rating = rating;
    }

    public int getProvider_id() {
        return provider_id;
    }

    public int getEbook_id() {
        return ebook_id;
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
