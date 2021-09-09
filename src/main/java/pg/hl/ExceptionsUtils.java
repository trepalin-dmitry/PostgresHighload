package pg.hl;

public class ExceptionsUtils {
    public static <T extends Throwable> T findCause(Class<T> clazz, Throwable throwable) {
        while (throwable != null) {
            if (clazz.isInstance(throwable)) {
                return clazz.cast(throwable);
            }

            throwable = throwable.getCause();
        }
        return null;
    }
}
