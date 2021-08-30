package pg.hl.test;

import java.lang.reflect.InvocationTargetException;

public interface TestItem {
    void run(RunArgument runArgument) throws InvocationTargetException, IllegalAccessException;
}

