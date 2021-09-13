package pg.hl.test.sp.bulk.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;

@Getter
@AllArgsConstructor
public class ExchangeDealPersonSourceExtended{
    private final ExchangeDealPersonSource dealPerson;
    private final ExchangeDealSource deal;
}


