package pg.hl.test.sp.bulk.multi.ee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.dto.multi.ExchangeDealPersonSource;
import pg.hl.dto.multi.ExchangeDealSource;

@Getter
@AllArgsConstructor
public class ExchangeDealPersonSourceExtended{
    private final ExchangeDealPersonSource dealPerson;
    private final ExchangeDealSource deal;
}


