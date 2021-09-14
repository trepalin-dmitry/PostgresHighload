package pg.hl.dto;

import lombok.ToString;

import java.util.Collection;

@ToString(callSuper = true)
public class ExchangeDealsPackage extends AbstractDataPackage<ExchangeDealSource> {
    public ExchangeDealsPackage(Collection<ExchangeDealSource> deals) {
        super();

        this.getListObjects().addAll(deals);
    }
}

