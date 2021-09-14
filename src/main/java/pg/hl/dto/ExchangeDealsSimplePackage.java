package pg.hl.dto;

import lombok.ToString;

import java.util.Collection;

@ToString(callSuper = true)
public class ExchangeDealsSimplePackage extends AbstractDataPackage<SimpleExchangeDealSource> {
    public ExchangeDealsSimplePackage(Collection<SimpleExchangeDealSource> deals) {
        super();

        this.getListObjects().addAll(deals);
    }
}