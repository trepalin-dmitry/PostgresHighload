package pg.hl.test.sp.bulk.mapping;

import pg.hl.test.sp.bulk.ee.ExchangeDealStatusInternalExtended;

public class ExchangeDealStatusInternalExtendedMapping extends AbstractMappingExtended<ExchangeDealStatusInternalExtended> {
    public ExchangeDealStatusInternalExtendedMapping() {
        super("public", "exchangeDealsStatusesInternal");

        mapUUID(MappingConstants.UPLOAD_KEY, s -> s.getDeal().getUploadKey());

        mapUUID("exchangeDealGuid", s -> s.getDeal().getDeal().getGuid());
        mapInteger("index", s -> s.getDealStatus().getIndex());
        mapText("typeId", s -> String.valueOf(s.getDealStatus().getTypeId()));
        mapTimeStamp("dateTime", s -> s.getDealStatus().getDateTime());
        mapText("comment", s -> s.getDealStatus().getComment());
    }
}
