package pg.hl.test;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface TestItem extends Closeable {
    void run(RunArgument runArgument) throws InvocationTargetException, IllegalAccessException, JsonProcessingException, SQLException;
}

