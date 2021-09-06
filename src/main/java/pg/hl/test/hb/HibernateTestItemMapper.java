package pg.hl.test.hb;

import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.hb.jpa.ExchangeDeal;
import pg.hl.test.hb.jpa.ExchangeDealPerson;
import pg.hl.test.hb.jpa.ExchangeDealStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HibernateTestItemMapper {
    private final HibernateTestItem testItem;

    protected HibernateTestItemMapper(HibernateTestItem testItem) {
        this.testItem = testItem;
    }

    private ExchangeDeal parse(ExchangeDealSource source) {
        return new ExchangeDeal().setGuid(source.getGuid())
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

    private ExchangeDealStatus parse(ExchangeDealStatusSource source) {
        return new ExchangeDealStatus()
                .setComment(source.getComment())
                .setDateTime(source.getDateTime())
                .setIndex(source.getIndex())
                .setType(testItem.resolve(source.getTypeCode()));
    }

    private ExchangeDealPerson parse(ExchangeDealPersonSource source) {
        return new ExchangeDealPerson()
                .setComment(source.getComment())
                .setPerson(testItem.resolve(source.getPersonGUId()));
    }

    public Collection<ExchangeDeal> parse(ExchangeDealsPackage exchangeDealsPackage) throws InvocationTargetException, IllegalAccessException {
        var deals = new ArrayList<ExchangeDeal>();

        for (ExchangeDealSource exchangeDealSource : exchangeDealsPackage.getObjects()) {
            deals.add(parse(exchangeDealSource));
        }

        return deals;
    }
}

