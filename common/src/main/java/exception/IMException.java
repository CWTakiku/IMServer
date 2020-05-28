package exception;

public class IMException extends RuntimeException {

    public IMException(String message, Throwable e) {
        super(message, e);
    }

    public IMException(Throwable e) {
        super(e);
    }

    public IMException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}