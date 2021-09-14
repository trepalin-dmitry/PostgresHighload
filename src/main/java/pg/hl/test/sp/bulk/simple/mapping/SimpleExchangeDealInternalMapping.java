package pg.hl.test.sp.bulk.simple.mapping;

import pg.hl.test.sp.bulk.AbstractMappingExtended;
import pg.hl.test.sp.ei.simple.SimpleExchangeDealInternal;

public class SimpleExchangeDealInternalMapping extends AbstractMappingExtended<SimpleExchangeDealInternal> {
    public SimpleExchangeDealInternalMapping() {
        super("simpleExchangeDealsInternal");

        mapUUID("guid", SimpleExchangeDealInternal::getGuid);
        mapUUID("accountGUId", SimpleExchangeDealInternal::getAccountGUId);
        mapInteger("typeId", SimpleExchangeDealInternal::getTypeId);
        mapText("directionCode", SimpleExchangeDealInternal::getDirectionCode);
        mapText("placeCode", SimpleExchangeDealInternal::getPlaceCode);
        mapUUID("tradeSessionGUId", SimpleExchangeDealInternal::getTradeSessionGUId);
        mapTimeStamp("dealDateTime", SimpleExchangeDealInternal::getDealDateTime);
        mapUUID("orderGUId", SimpleExchangeDealInternal::getOrderGUId);
        mapUUID("instrumentGUId", SimpleExchangeDealInternal::getInstrumentGUId);
        mapUUID("currencyGUId", SimpleExchangeDealInternal::getCurrencyGUId);
        mapNumeric("quantity", SimpleExchangeDealInternal::getQuantity);
        mapNumeric("price", SimpleExchangeDealInternal::getPrice);
        mapNumeric("volume", SimpleExchangeDealInternal::getVolume);
        mapUUID("couponCurrencyGUId", SimpleExchangeDealInternal::getCouponCurrencyGUId);
        mapNumeric("couponVolume", SimpleExchangeDealInternal::getCouponVolume);
        mapDate("planDeliveryDate", SimpleExchangeDealInternal::getPlanDeliveryDate);
        mapDate("planPaymentDate", SimpleExchangeDealInternal::getPlanPaymentDate);
    }
}
