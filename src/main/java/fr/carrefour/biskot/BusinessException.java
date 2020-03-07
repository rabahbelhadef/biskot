package fr.carrefour.biskot;

public class BusinessException extends RuntimeException {
    public BusinessException(String message, Object... args) {
        super(String.format(message, args));
    }
}
