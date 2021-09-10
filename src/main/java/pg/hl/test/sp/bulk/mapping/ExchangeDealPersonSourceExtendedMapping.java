package pg.hl.test.sp.bulk.mapping;

import pg.hl.test.sp.bulk.ee.ExchangeDealPersonSourceExtended;

public class ExchangeDealPersonSourceExtendedMapping extends AbstractMappingExtended<ExchangeDealPersonSourceExtended> {
    public ExchangeDealPersonSourceExtendedMapping() {
        super("public", "exchangeDealsPersonsSource");

        mapUUID(MappingConstants.UPLOAD_KEY, s -> s.getDeal().getUploadKey());

        mapUUID("exchangeDealGuid", s -> s.getDeal().getDeal().getGuid());
        mapUUID("personGuid", s -> s.getDealPerson().getPersonGUId());
        mapText("comment", s -> s.getDealPerson().getComment());
    }
}


