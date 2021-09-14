package pg.hl.test.sp.bulk.multi.mapping;

import pg.hl.test.sp.bulk.AbstractMappingExtended;
import pg.hl.test.sp.bulk.multi.ee.ExchangeDealPersonSourceExtended;

public class ExchangeDealPersonSourceExtendedMapping extends AbstractMappingExtended<ExchangeDealPersonSourceExtended> {
    public ExchangeDealPersonSourceExtendedMapping() {
        super("exchangeDealsPersonsSource");

        mapUUID("exchangeDealGuid", s -> s.getDeal().getGuid());
        mapUUID("personGuid", s -> s.getDealPerson().getPersonGUId());
        mapText("comment", s -> s.getDealPerson().getComment());
    }
}


