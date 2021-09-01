package pg.hl.test;

import pg.hl.dto.ExchangeDealsPackage;

public abstract class AbstractTestItem implements TestItem {

    @Override
    public void run(RunArgument runArgument) throws ProxyException {
        uploadDeals(runArgument.getDealsPackage());
    }

    protected abstract void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws ProxyException;

    @Override
    public void close() {
        internalClose();
    }

    protected void internalClose(){}
}

