package pg.hl.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import pg.hl.dto.ExchangeDealsPackage;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public abstract class AbstractTestItem implements TestItem {

    @Override
    public void run(RunArgument runArgument) throws InvocationTargetException, IllegalAccessException, JsonProcessingException, SQLException {
        uploadDeals(runArgument.getDealsPackage());
    }

    protected abstract void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws InvocationTargetException, IllegalAccessException, JsonProcessingException, SQLException;

    @Override
    public void close() {
        internalClose();
    }

    protected void internalClose(){}
}
