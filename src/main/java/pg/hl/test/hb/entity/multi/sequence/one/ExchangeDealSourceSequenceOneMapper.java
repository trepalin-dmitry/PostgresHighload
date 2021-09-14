package pg.hl.test.hb.entity.multi.sequence.one;

import pg.hl.dto.multi.ExchangeDealPersonSource;
import pg.hl.dto.multi.ExchangeDealSource;
import pg.hl.dto.multi.ExchangeDealStatusSource;
import pg.hl.test.hb.HibernateResolver;
import pg.hl.test.hb.HibernateTestItemMapper;

public class ExchangeDealSourceSequenceOneMapper extends HibernateTestItemMapper<ExchangeDealSource, ExchangeDealSequenceOne> {

    public ExchangeDealSourceSequenceOneMapper(HibernateResolver testItem) {
        super(testItem);
    }

    @Override
    protected ExchangeDealSequenceOne parse(ExchangeDealSource source) {
        return new ExchangeDealSequenceOne().setGuid(source.getGuid())
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

    private ExchangeDealStatusSequenceOne parse(ExchangeDealStatusSource source) {
        return new ExchangeDealStatusSequenceOne()
                .setComment(source.getComment())
                .setDateTime(source.getDateTime())
                .setIndex(source.getIndex())
                .setType(getResolver().resolveStatusType(source.getTypeCode()));
    }

    private ExchangeDealPersonSequenceOne parse(ExchangeDealPersonSource source) {
        return new ExchangeDealPersonSequenceOne()
                .setComment(source.getComment())
                .setPerson(getResolver().resolvePerson(source.getPersonGUId()));
    }
}