package pg.hl.test.hb.entity.simple.identity;

import pg.hl.dto.simple.SimpleExchangeDealSource;
import pg.hl.test.hb.HibernateResolver;
import pg.hl.test.hb.HibernateTestItemMapper;

public class SimpleExchangeDealSourceIdentityMapper extends HibernateTestItemMapper<SimpleExchangeDealSource, SimpleExchangeDealIdentity> {
    public SimpleExchangeDealSourceIdentityMapper(HibernateResolver testItem) {
        super(testItem);
    }

    @Override
    protected SimpleExchangeDealIdentity parse(SimpleExchangeDealSource source) {
        return new SimpleExchangeDealIdentity().setGuid(source.getGuid())
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
