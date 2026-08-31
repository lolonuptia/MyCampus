package com.mycampus.backend.exception;

// Levée quand un étudiant/cours/note demandé n'existe pas -> déclenche un 404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
