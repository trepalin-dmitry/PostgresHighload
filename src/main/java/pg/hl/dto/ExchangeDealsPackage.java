package pg.hl.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.Collection;
import java.util.UUID;

@ToString(callSuper = true)
public class ExchangeDealsPackage extends AbstractDataPackage<ExchangeDealSource> {
    @Getter
    private final UUID guid = UUID.randomUUID();

    public ExchangeDealsPackage(Collection<ExchangeDealSource> deals){
        getListObjects().addAll(deals);
    }
}
