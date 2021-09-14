package pg.hl.test.sp.bulk.multi.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.dto.multi.ExchangeDealSource;
import pg.hl.dto.multi.ExchangeDealStatusSource;

@Getter
@AllArgsConstructor
public class ExchangeDealStatusSourceExtended {
    private final ExchangeDealStatusSource dealStatus;
    private final ExchangeDealSource deal;
}


