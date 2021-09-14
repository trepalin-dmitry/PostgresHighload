package pg.hl.test.sp.bulk.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.test.ei.ExchangeDealInternal;
import pg.hl.test.ei.ExchangeDealStatusInternal;

@Getter
@AllArgsConstructor
public class ExchangeDealStatusInternalExtended {
    private final ExchangeDealStatusInternal dealStatus;
    private final ExchangeDealInternal deal;
}
