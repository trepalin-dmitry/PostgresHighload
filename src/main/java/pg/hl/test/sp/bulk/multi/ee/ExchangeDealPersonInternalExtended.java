package pg.hl.test.sp.bulk.multi.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.test.sp.ei.multi.ExchangeDealInternal;
import pg.hl.test.sp.ei.multi.ExchangeDealPersonInternal;

@Getter
@AllArgsConstructor
public class ExchangeDealPersonInternalExtended {
    private final ExchangeDealPersonInternal dealPerson;
    private final ExchangeDealInternal deal;
}
