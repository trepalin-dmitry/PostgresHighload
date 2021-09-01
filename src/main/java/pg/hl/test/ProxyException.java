package pg.hl.test;

public class ProxyException extends Exception {
    public ProxyException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}

