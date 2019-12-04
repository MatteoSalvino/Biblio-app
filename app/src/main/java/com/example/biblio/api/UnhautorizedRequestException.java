package com.example.biblio.api;

public class UnhautorizedRequestException extends Exception {
    UnhautorizedRequestException(String message) {
        super(message);
    }
}
