package pg.hl.test.sp.bulk.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.test.sp.ei.ExchangeDealInternal;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExchangeDealInternalExtended {
    private final ExchangeDealInternal deal;
    private final UUID uploadKey;
}
