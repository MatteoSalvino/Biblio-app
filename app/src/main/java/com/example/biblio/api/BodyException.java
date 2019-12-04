package com.example.biblio.api;

class BodyException extends Exception {
    static final String NULL_BODY_MSG = "invalid response body";

    BodyException(String message) {
        super(message);
    }
}
