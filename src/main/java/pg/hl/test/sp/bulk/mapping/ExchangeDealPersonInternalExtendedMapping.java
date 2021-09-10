package pg.hl.test.sp.bulk.mapping;

import pg.hl.test.sp.bulk.ee.ExchangeDealPersonInternalExtended;

public class ExchangeDealPersonInternalExtendedMapping extends AbstractMappingExtended<ExchangeDealPersonInternalExtended> {
    public ExchangeDealPersonInternalExtendedMapping() {
        super("public", "exchangeDealsPersonsInternal");

        mapUUID(MappingConstants.UPLOAD_KEY, s -> s.getDeal().getUploadKey());

        mapUUID("exchangeDealGuid", s -> s.getDeal().getDeal().getGuid());
        mapInteger("personId", s -> s.getDealPerson().getPersonId());
        mapText("comment", s -> s.getDealPerson().getComment());
    }
}
