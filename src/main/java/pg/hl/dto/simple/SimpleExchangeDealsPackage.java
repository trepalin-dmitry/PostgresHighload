package pg.hl.dto.simple;

import lombok.ToString;
import pg.hl.dto.AbstractDataPackage;

import java.util.Collection;

@ToString(callSuper = true)
public class SimpleExchangeDealsPackage extends AbstractDataPackage<SimpleExchangeDealSource> {
    public SimpleExchangeDealsPackage(Collection<SimpleExchangeDealSource> deals) {
        super();

        this.getListObjects().addAll(deals);
    }
}