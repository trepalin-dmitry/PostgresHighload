package pg.hl.test;

import javax.management.OperationsException;
import java.lang.reflect.InvocationTargetException;

public interface TestItem {
    void cleanDatabase();

    void createPackage() throws OperationsException;

    void run() throws InvocationTargetException, IllegalAccessException;
}
