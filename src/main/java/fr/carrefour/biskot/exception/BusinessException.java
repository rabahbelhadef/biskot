package fr.carrefour.biskot.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message, Object... args) {
        super(String.format(message, args));
    }
}
