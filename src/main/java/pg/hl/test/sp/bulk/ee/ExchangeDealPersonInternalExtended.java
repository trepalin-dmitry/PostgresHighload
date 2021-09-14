package pg.hl.test.sp.bulk.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.test.ei.ExchangeDealInternal;
import pg.hl.test.ei.ExchangeDealPersonInternal;

@Getter
@AllArgsConstructor
public class ExchangeDealPersonInternalExtended {
    private final ExchangeDealPersonInternal dealPerson;
    private final ExchangeDealInternal deal;
}
