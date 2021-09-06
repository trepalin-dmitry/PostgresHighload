package pg.hl.test.sp;

import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.ExistsDataController;
import pg.hl.test.sp.ei.ExchangeDealInternal;
import pg.hl.test.sp.ei.ExchangeDealPersonInternal;
import pg.hl.test.sp.ei.ExchangeDealStatusInternal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StoredProcedureTestItemMapper {
    protected StoredProcedureTestItemMapper() {
    }

    private ExchangeDealInternal parse(ExchangeDealSource source) {
        return new ExchangeDealInternal()
                .setGuid(source.getGuid())
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

    private <TypeSource, TypeTarget> Collection<TypeTarget> parse(Collection<TypeSource> source, Function<TypeSource, TypeTarget> function) {
        return source.stream().map(function).collect(Collectors.toList());
    }

    private ExchangeDealStatusInternal parse(ExchangeDealStatusSource source) {
        return new ExchangeDealStatusInternal()
                .setComment(source.getComment())
                .setDateTime(source.getDateTime())
                .setIndex(source.getIndex())
                .setTypeId(ExistsDataController.resolveStatusType(source.getTypeCode()));
    }

    private ExchangeDealPersonInternal parse(ExchangeDealPersonSource source) {
        return new ExchangeDealPersonInternal()
                .setComment(source.getComment())
                .setPersonId(ExistsDataController.resolvePerson(source.getPersonGUId()));
    }

    public Collection<ExchangeDealInternal> parse(ExchangeDealsPackage exchangeDealsPackage) {
        var deals = new ArrayList<ExchangeDealInternal>();

        for (ExchangeDealSource exchangeDealSource : exchangeDealsPackage.getObjects()) {
            deals.add(parse(exchangeDealSource));
        }

        return deals;
    }
}

