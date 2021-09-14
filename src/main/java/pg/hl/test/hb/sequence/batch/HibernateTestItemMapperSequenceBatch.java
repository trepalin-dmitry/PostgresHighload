package pg.hl.test.hb.sequence.batch;

import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.test.hb.HibernateResolver;
import pg.hl.test.hb.HibernateTestItemMapper;

public class HibernateTestItemMapperSequenceBatch extends HibernateTestItemMapper<ExchangeDealSource, ExchangeDealSequenceBatch> {
    protected HibernateTestItemMapperSequenceBatch(HibernateResolver testItem) {
        super(testItem);
    }

    @Override
    protected ExchangeDealSequenceBatch parse(ExchangeDealSource source) {
        return new ExchangeDealSequenceBatch().setGuid(source.getGuid())
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

    private ExchangeDealStatusSequenceBatch parse(ExchangeDealStatusSource source) {
        return new ExchangeDealStatusSequenceBatch()
                .setComment(source.getComment())
                .setDateTime(source.getDateTime())
                .setIndex(source.getIndex())
                .setType(getResolver().resolveStatusType(source.getTypeCode()));
    }

    private ExchangeDealPersonSequenceBatch parse(ExchangeDealPersonSource source) {
        return new ExchangeDealPersonSequenceBatch()
                .setComment(source.getComment())
                .setPerson(getResolver().resolvePerson(source.getPersonGUId()));
    }
}