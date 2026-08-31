package com.mycampus.backend.exception;

// Levée quand les données envoyées sont invalides (ex: email déjà utilisé) -> déclenche un 400/409
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
