package pg.hl.test.sp.bulk.mapping;

import pg.hl.test.sp.bulk.ee.ExchangeDealPersonSourceExtended;

public class ExchangeDealPersonSourceExtendedMapping extends AbstractMappingExtended<ExchangeDealPersonSourceExtended> {
    public ExchangeDealPersonSourceExtendedMapping() {
        super("exchangeDealsPersonsSource");

        mapUUID("exchangeDealGuid", s -> s.getDeal().getGuid());
        mapUUID("personGuid", s -> s.getDealPerson().getPersonGUId());
        mapText("comment", s -> s.getDealPerson().getComment());
    }
}


