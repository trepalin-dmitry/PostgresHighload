package pg.hl.test.sp.bulk.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.dto.ExchangeDealSource;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExchangeDealSourceExtended {
    private final ExchangeDealSource deal;
    private final UUID uploadKey;
}

