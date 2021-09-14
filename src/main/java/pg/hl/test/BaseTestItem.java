package pg.hl.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import pg.hl.DevException;

import java.sql.SQLException;

public abstract class BaseTestItem implements TestItem {
    @Override
    public void run(RunArgument runArgument) throws SQLException, JsonProcessingException {
        if (runArgument == null) {
            throw new DevException("runArgument == null");
        }

        uploadDealsCore(runArgument.getDealsPackage());
    }

    protected abstract void uploadDealsCore(Object dataPackage) throws JsonProcessingException, SQLException;

    @Override
    public void close() throws SQLException {
    }
}
