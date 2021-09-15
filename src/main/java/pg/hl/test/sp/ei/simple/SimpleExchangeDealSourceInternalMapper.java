package pg.hl.test.sp.ei.simple;

import pg.hl.dto.simple.SimpleExchangeDealSource;
import pg.hl.dto.simple.SimpleExchangeDealsPackage;
import pg.hl.test.EntityType;
import pg.hl.test.IdentityStrategy;
import pg.hl.test.sp.ei.AbstractMapper;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class SimpleExchangeDealSourceInternalMapper extends AbstractMapper<SimpleExchangeDealsPackage, SimpleExchangeDealSource, SimpleExchangeDealInternal> {
    public SimpleExchangeDealSourceInternalMapper(IdentityStrategy identityStrategy, EntityType entityType) throws SQLException, PropertyVetoException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        super(identityStrategy, entityType);
    }

    @Override
    protected SimpleExchangeDealInternal parse(SimpleExchangeDealSource source) {
        return new SimpleExchangeDealInternal()
                .setGuid(source.getGuid())
                .setAccountGUId(source.getAccountGUId())
                .setTypeId(getExistsDataController().getDealsTypes().resolve(source.getTypeCode()))
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

