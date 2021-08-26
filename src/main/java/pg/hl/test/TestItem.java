package pg.hl.test;

import java.lang.reflect.InvocationTargetException;

public interface TestItem {
    void cleanDatabase();

    void createPackage();

    void run() throws InvocationTargetException, IllegalAccessException;
}
