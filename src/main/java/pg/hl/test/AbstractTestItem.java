package pg.hl.test;

import pg.hl.dto.ExchangeDealsPackage;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractTestItem implements TestItem {

    @Override
    public void run(RunArgument runArgument) throws InvocationTargetException, IllegalAccessException {
        uploadDeals(runArgument.getDealsPackage());
    }

    protected abstract void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws InvocationTargetException, IllegalAccessException;
}
