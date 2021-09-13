package pg.hl.test.sp.bulk.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.dto.ExchangeDealStatusSource;

@Getter
@AllArgsConstructor
public class ExchangeDealStatusSourceExtended {
    private final ExchangeDealStatusSource dealStatus;
    private final ExchangeDealSourceExtended deal;
}


