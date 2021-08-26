package pg.hl.dto;

import java.util.Collection;

public class ExchangeDealsPackage extends AbstractDataPackage<ExchangeDealSource> {
    public ExchangeDealsPackage(Collection<ExchangeDealSource> deals){
        getListObjects().addAll(deals);
    }
}
