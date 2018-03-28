package net.windia.insdata.exception;

public class InsDataException extends RuntimeException {

    private Error error;

    public InsDataException(String message) {
        super(message);
        this.error = new Error(message);
    }

    public InsDataException(String message, Throwable cause) {
        super(message, cause);
        this.error = new Error(message);
    }

    public InsDataException(Throwable cause) {
        super(cause);
    }

    public Error getError() {
        return error;
    }
}
