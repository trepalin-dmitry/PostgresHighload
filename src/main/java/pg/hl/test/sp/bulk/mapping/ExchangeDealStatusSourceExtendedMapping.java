package pg.hl.test.sp.bulk.mapping;

import pg.hl.test.sp.bulk.ee.ExchangeDealStatusSourceExtended;

public class ExchangeDealStatusSourceExtendedMapping extends AbstractMappingExtended<ExchangeDealStatusSourceExtended> {
    public ExchangeDealStatusSourceExtendedMapping() {
        super("exchangeDealsStatusesSource");

        mapUUID("exchangeDealGuid", s -> s.getDeal().getGuid());
        mapInteger("index", s -> s.getDealStatus().getIndex());
        mapText("typeCode", s -> s.getDealStatus().getTypeCode());
        mapTimeStamp("dateTime", s -> s.getDealStatus().getDateTime());
        mapText("comment", s -> s.getDealStatus().getComment());
    }
}

