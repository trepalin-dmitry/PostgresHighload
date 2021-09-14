package pg.hl.test.sp.bulk.multi.mapping;

import pg.hl.test.sp.bulk.AbstractMappingExtended;
import pg.hl.test.sp.bulk.multi.ee.ExchangeDealStatusInternalExtended;

public class ExchangeDealStatusInternalExtendedMapping extends AbstractMappingExtended<ExchangeDealStatusInternalExtended> {
    public ExchangeDealStatusInternalExtendedMapping() {
        super("exchangeDealsStatusesInternal");

        mapUUID("exchangeDealGuid", s -> s.getDeal().getGuid());
        mapInteger("index", s -> s.getDealStatus().getIndex());
        mapText("typeId", s -> String.valueOf(s.getDealStatus().getTypeId()));
        mapTimeStamp("dateTime", s -> s.getDealStatus().getDateTime());
        mapText("comment", s -> s.getDealStatus().getComment());
    }
}