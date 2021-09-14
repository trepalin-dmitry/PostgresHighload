package pg.hl.test.sp.bulk.multi.mapping;

import pg.hl.dto.AbstractDataObjectWithGuidAndRevision;
import pg.hl.dto.multi.ExchangeDealSource;
import pg.hl.test.sp.bulk.AbstractMappingExtended;

public class ExchangeDealSourceMapping extends AbstractMappingExtended<ExchangeDealSource> {
    public ExchangeDealSourceMapping() {
        super("exchangeDealsSource");

        mapUUID("guid", AbstractDataObjectWithGuidAndRevision::getGuid);
        mapUUID("accountGUId", ExchangeDealSource::getAccountGUId);
        mapText("typeCode", ExchangeDealSource::getTypeCode);
        mapText("directionCode", ExchangeDealSource::getDirectionCode);
        mapText("placeCode", ExchangeDealSource::getPlaceCode);
        mapUUID("tradeSessionGUId", ExchangeDealSource::getTradeSessionGUId);
        mapTimeStamp("dealDateTime", ExchangeDealSource::getDealDateTime);
        mapUUID("orderGUId", ExchangeDealSource::getOrderGUId);
        mapUUID("instrumentGUId", ExchangeDealSource::getInstrumentGUId);
        mapUUID("currencyGUId", ExchangeDealSource::getCurrencyGUId);
        mapNumeric("quantity", ExchangeDealSource::getQuantity);
        mapNumeric("price", ExchangeDealSource::getPrice);
        mapNumeric("volume", ExchangeDealSource::getVolume);
        mapUUID("couponCurrencyGUId", ExchangeDealSource::getCouponCurrencyGUId);
        mapNumeric("couponVolume", ExchangeDealSource::getCouponVolume);
        mapDate("planDeliveryDate", ExchangeDealSource::getPlanDeliveryDate);
        mapDate("planPaymentDate", ExchangeDealSource::getPlanPaymentDate);
    }
}

