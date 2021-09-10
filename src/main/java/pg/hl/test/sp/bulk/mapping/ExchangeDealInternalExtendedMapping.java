package pg.hl.test.sp.bulk.mapping;

import pg.hl.test.sp.bulk.ee.ExchangeDealInternalExtended;

public class ExchangeDealInternalExtendedMapping extends AbstractMappingExtended<ExchangeDealInternalExtended> {
    public ExchangeDealInternalExtendedMapping() {
        super("public", "exchangeDealsInternal");

        mapUUID(MappingConstants.UPLOAD_KEY, ExchangeDealInternalExtended::getUploadKey);

        mapUUID("guid", s -> s.getDeal().getGuid());
        mapUUID("accountGUId", s -> s.getDeal().getAccountGUId());
        mapText("typeCode", s -> s.getDeal().getTypeCode());
        mapText("directionCode", s -> s.getDeal().getDirectionCode());
        mapText("placeCode", s -> s.getDeal().getPlaceCode());
        mapUUID("tradeSessionGUId", s -> s.getDeal().getTradeSessionGUId());
        mapTimeStamp("dealDateTime", s -> s.getDeal().getDealDateTime());
        mapUUID("orderGUId", s -> s.getDeal().getOrderGUId());
        mapUUID("instrumentGUId", s -> s.getDeal().getInstrumentGUId());
        mapUUID("currencyGUId", s -> s.getDeal().getCurrencyGUId());
        mapNumeric("quantity", s -> s.getDeal().getQuantity());
        mapNumeric("price", s -> s.getDeal().getPrice());
        mapNumeric("volume", s -> s.getDeal().getVolume());
        mapUUID("couponCurrencyGUId", s -> s.getDeal().getCouponCurrencyGUId());
        mapNumeric("couponVolume", s -> s.getDeal().getCouponVolume());
        mapDate("planDeliveryDate", s -> s.getDeal().getPlanDeliveryDate());
        mapDate("planPaymentDate", s -> s.getDeal().getPlanPaymentDate());
    }
}
