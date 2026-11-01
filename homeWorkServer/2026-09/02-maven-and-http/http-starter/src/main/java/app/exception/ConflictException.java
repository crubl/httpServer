package app.exception;

public class ConflictException extends DomainException {
    public ConflictException(String message) {
        super(message);
    }
}
