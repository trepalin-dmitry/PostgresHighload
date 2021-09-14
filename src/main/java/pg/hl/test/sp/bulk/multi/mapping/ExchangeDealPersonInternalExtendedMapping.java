package pg.hl.test.sp.bulk.multi.mapping;

import pg.hl.test.sp.bulk.AbstractMappingExtended;
import pg.hl.test.sp.bulk.multi.ee.ExchangeDealPersonInternalExtended;

public class ExchangeDealPersonInternalExtendedMapping extends AbstractMappingExtended<ExchangeDealPersonInternalExtended> {
    public ExchangeDealPersonInternalExtendedMapping() {
        super("exchangeDealsPersonsInternal");

        mapUUID("exchangeDealGuid", s -> s.getDeal().getGuid());
        mapInteger("personId", s -> s.getDealPerson().getPersonId());
        mapText("comment", s -> s.getDealPerson().getComment());
    }
}
