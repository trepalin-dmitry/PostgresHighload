package pg.hl.test;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;

public interface TestItem {
    void run(RunArgument runArgument) throws SQLException, JsonProcessingException;

    void close() throws SQLException;
}

