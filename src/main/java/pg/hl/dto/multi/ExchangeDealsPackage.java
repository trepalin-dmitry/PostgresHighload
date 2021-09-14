package pg.hl.dto.multi;

import lombok.ToString;
import pg.hl.dto.AbstractDataPackage;

import java.util.Collection;

@ToString(callSuper = true)
public class ExchangeDealsPackage extends AbstractDataPackage<ExchangeDealSource> {
    public ExchangeDealsPackage(Collection<ExchangeDealSource> deals) {
        super();

        this.getListObjects().addAll(deals);
    }
}

