package pg.hl.test.sp.bulk.simple.mapping;

import pg.hl.dto.AbstractDataObjectWithGuidAndRevision;
import pg.hl.dto.simple.SimpleExchangeDealSource;
import pg.hl.test.sp.bulk.AbstractMappingExtended;

public class SimpleExchangeDealSourceMapping extends AbstractMappingExtended<SimpleExchangeDealSource> {
    public SimpleExchangeDealSourceMapping() {
        super("simpleExchangeDealsSource");

        mapUUID("guid", AbstractDataObjectWithGuidAndRevision::getGuid);
        mapUUID("accountGUId", SimpleExchangeDealSource::getAccountGUId);
        mapText("typeCode", SimpleExchangeDealSource::getTypeCode);
        mapText("directionCode", SimpleExchangeDealSource::getDirectionCode);
        mapText("placeCode", SimpleExchangeDealSource::getPlaceCode);
        mapUUID("tradeSessionGUId", SimpleExchangeDealSource::getTradeSessionGUId);
        mapTimeStamp("dealDateTime", SimpleExchangeDealSource::getDealDateTime);
        mapUUID("orderGUId", SimpleExchangeDealSource::getOrderGUId);
        mapUUID("instrumentGUId", SimpleExchangeDealSource::getInstrumentGUId);
        mapUUID("currencyGUId", SimpleExchangeDealSource::getCurrencyGUId);
        mapNumeric("quantity", SimpleExchangeDealSource::getQuantity);
        mapNumeric("price", SimpleExchangeDealSource::getPrice);
        mapNumeric("volume", SimpleExchangeDealSource::getVolume);
        mapUUID("couponCurrencyGUId", SimpleExchangeDealSource::getCouponCurrencyGUId);
        mapNumeric("couponVolume", SimpleExchangeDealSource::getCouponVolume);
        mapDate("planDeliveryDate", SimpleExchangeDealSource::getPlanDeliveryDate);
        mapDate("planPaymentDate", SimpleExchangeDealSource::getPlanPaymentDate);
    }
}

