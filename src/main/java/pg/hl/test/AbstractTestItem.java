package pg.hl.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import pg.hl.DevException;
import pg.hl.dto.ExchangeDealsPackage;

import java.sql.SQLException;

public abstract class AbstractTestItem implements TestItem {
    @Override
    public void run(RunArgument runArgument) throws SQLException, JsonProcessingException {
        if (runArgument == null){
            throw new DevException("runArgument == null");
        }

        uploadDeals(runArgument.getDealsPackage());
    }

    protected abstract void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws JsonProcessingException, SQLException;

    @Override
    public void close() {
        internalClose();
    }

    protected void internalClose() {
    }
}

