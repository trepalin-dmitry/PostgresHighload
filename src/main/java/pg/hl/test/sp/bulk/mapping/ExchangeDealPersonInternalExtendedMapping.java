package pg.hl.test.sp.bulk.mapping;

import pg.hl.test.sp.bulk.ee.ExchangeDealPersonInternalExtended;

public class ExchangeDealPersonInternalExtendedMapping extends AbstractMappingExtended<ExchangeDealPersonInternalExtended> {
    public ExchangeDealPersonInternalExtendedMapping() {
        super("exchangeDealsPersonsInternal");

        mapUUID("exchangeDealGuid", s -> s.getDeal().getGuid());
        mapInteger("personId", s -> s.getDealPerson().getPersonId());
        mapText("comment", s -> s.getDealPerson().getComment());
    }
}
