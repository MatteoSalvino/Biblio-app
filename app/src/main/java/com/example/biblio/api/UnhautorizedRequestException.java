package com.example.biblio.api;

class UnhautorizedRequestException extends Exception {
    UnhautorizedRequestException(String message) {
        super(message);
    }
}
