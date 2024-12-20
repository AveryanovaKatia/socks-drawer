package ru.project.exception;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(final String message) {
        super(message);
    }
}
