package pg.hl.test.hb.entity.simple.sequence.batch;

import pg.hl.dto.simple.SimpleExchangeDealSource;
import pg.hl.test.hb.HibernateResolver;
import pg.hl.test.hb.HibernateTestItemMapper;

public class SimpleExchangeDealSourceSequenceBatchMapper extends HibernateTestItemMapper<SimpleExchangeDealSource, SimpleExchangeDealSequenceBatch> {
    public SimpleExchangeDealSourceSequenceBatchMapper(HibernateResolver testItem) {
        super(testItem);
    }

    @Override
    protected SimpleExchangeDealSequenceBatch parse(SimpleExchangeDealSource source) {
        return new SimpleExchangeDealSequenceBatch().setGuid(source.getGuid())
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