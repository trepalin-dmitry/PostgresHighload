package pg.hl.test.hb.identity;

import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.test.hb.HibernateResolver;
import pg.hl.test.hb.HibernateTestItemMapper;

public class HibernateTestItemMapperIdentity extends HibernateTestItemMapper<ExchangeDealSource, ExchangeDealIdentity> {
    protected HibernateTestItemMapperIdentity(HibernateResolver testItem) {
        super(testItem);
    }

    @Override
    protected ExchangeDealIdentity parse(ExchangeDealSource source) {
        return new ExchangeDealIdentity().setGuid(source.getGuid())
                .setAccountGUId(source.getAccountGUId())
                .setType(getResolver().resolveDealType(source.getTypeCode()))
                .setDirectionCode(source.getDirectionCode())
                .setPlaceCode(source.getPlaceCode())
                .setTradeSessionGUId(source.getTradeSessionGUId())
                .setDealDateTime(source.getDealDateTime())
                .setOrderGUId(source.getOrderGUId())
                .setInstrumentGUId(source.getInstrumentGUId())
                .setCurrencyGUId(source.getCurrencyGUId())
                .setQuantity(source.getQuantity())
                .setPrice(source.getPrice())
                .setCouponCurrencyGUId(source.getCouponCurrencyGUId())
                .setCouponVolume(source.getCouponVolume())
                .setPlanDeliveryDate(source.getPlanDeliveryDate())
                .setPlanPaymentDate(source.getPlanPaymentDate())
                .setVolume(source.getVolume())
                .addPersonsAll(parse(source.getPersons(), this::parse))
                .addStatusesAll(parse(source.getStatuses(), this::parse));
    }

    private ExchangeDealStatusIdentity parse(ExchangeDealStatusSource source) {
        return new ExchangeDealStatusIdentity()
                .setComment(source.getComment())
                .setDateTime(source.getDateTime())
                .setIndex(source.getIndex())
                .setType(getResolver().resolveStatusType(source.getTypeCode()));
    }

    private ExchangeDealPersonIdentity parse(ExchangeDealPersonSource source) {
        return new ExchangeDealPersonIdentity()
                .setComment(source.getComment())
                .setPerson(getResolver().resolvePerson(source.getPersonGUId()));
    }
}
