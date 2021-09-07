package pg.hl.test.hb.sequence;

import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.HibernateTestItemMapper;

public class HibernateTestItemMapperSequence extends HibernateTestItemMapper<ExchangeDealSequence> {
    protected HibernateTestItemMapperSequence(HibernateTestItem<ExchangeDealSequence> testItem) {
        super(testItem);
    }

    @Override
    protected ExchangeDealSequence parse(ExchangeDealSource source) {
        return new ExchangeDealSequence().setGuid(source.getGuid())
                .setAccountGUId(source.getAccountGUId())
                .setTypeCode(source.getTypeCode())
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

    private ExchangeDealStatusSequence parse(ExchangeDealStatusSource source) {
        return new ExchangeDealStatusSequence()
                .setComment(source.getComment())
                .setDateTime(source.getDateTime())
                .setIndex(source.getIndex())
                .setType(getTestItem().resolve(source.getTypeCode()));
    }

    private ExchangeDealPersonSequence parse(ExchangeDealPersonSource source) {
        return new ExchangeDealPersonSequence()
                .setComment(source.getComment())
                .setPerson(getTestItem().resolve(source.getPersonGUId()));
    }
}