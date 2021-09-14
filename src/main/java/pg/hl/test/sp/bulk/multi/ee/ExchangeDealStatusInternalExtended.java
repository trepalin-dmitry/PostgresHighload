package pg.hl.test.sp.bulk.multi.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.test.sp.ei.multi.ExchangeDealInternal;
import pg.hl.test.sp.ei.multi.ExchangeDealStatusInternal;

@Getter
@AllArgsConstructor
public class ExchangeDealStatusInternalExtended {
    private final ExchangeDealStatusInternal dealStatus;
    private final ExchangeDealInternal deal;
}
