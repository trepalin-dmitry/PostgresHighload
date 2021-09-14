package pg.hl.test.hb.entity.simple.sequence.one;

import pg.hl.dto.simple.SimpleExchangeDealSource;
import pg.hl.test.hb.HibernateResolver;
import pg.hl.test.hb.HibernateTestItemMapper;

public class SimpleExchangeDealSourceSequenceOneMapper extends HibernateTestItemMapper<SimpleExchangeDealSource, SimpleExchangeDealSequenceOne> {

    public SimpleExchangeDealSourceSequenceOneMapper(HibernateResolver testItem) {
        super(testItem);
    }

    @Override
    protected SimpleExchangeDealSequenceOne parse(SimpleExchangeDealSource source) {
        return new SimpleExchangeDealSequenceOne().setGuid(source.getGuid())
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
                .setVolume(source.getVolume());
    }
}