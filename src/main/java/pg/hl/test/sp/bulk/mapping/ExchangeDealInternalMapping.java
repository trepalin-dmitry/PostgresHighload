package pg.hl.test.sp.bulk.mapping;

import pg.hl.test.sp.ei.ExchangeDealInternal;

public class ExchangeDealInternalMapping extends AbstractMappingExtended<ExchangeDealInternal> {
    public ExchangeDealInternalMapping() {
        super("exchangeDealsInternal");

        mapUUID("guid", ExchangeDealInternal::getGuid);
        mapUUID("accountGUId", ExchangeDealInternal::getAccountGUId);
        mapText("typeCode", ExchangeDealInternal::getTypeCode);
        mapText("directionCode", ExchangeDealInternal::getDirectionCode);
        mapText("placeCode", ExchangeDealInternal::getPlaceCode);
        mapUUID("tradeSessionGUId", ExchangeDealInternal::getTradeSessionGUId);
        mapTimeStamp("dealDateTime", ExchangeDealInternal::getDealDateTime);
        mapUUID("orderGUId", ExchangeDealInternal::getOrderGUId);
        mapUUID("instrumentGUId", ExchangeDealInternal::getInstrumentGUId);
        mapUUID("currencyGUId", ExchangeDealInternal::getCurrencyGUId);
        mapNumeric("quantity", ExchangeDealInternal::getQuantity);
        mapNumeric("price", ExchangeDealInternal::getPrice);
        mapNumeric("volume", ExchangeDealInternal::getVolume);
        mapUUID("couponCurrencyGUId", ExchangeDealInternal::getCouponCurrencyGUId);
        mapNumeric("couponVolume", ExchangeDealInternal::getCouponVolume);
        mapDate("planDeliveryDate", ExchangeDealInternal::getPlanDeliveryDate);
        mapDate("planPaymentDate", ExchangeDealInternal::getPlanPaymentDate);
    }
}
