package saka1029.csp;

public class CSPException extends RuntimeException {
    public CSPException(String format, Object... args) {
        super(format.formatted(args));
    }

    public CSPException(Throwable t) {
        super(t);
    }
}
