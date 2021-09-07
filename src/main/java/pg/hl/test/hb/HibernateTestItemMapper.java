package pg.hl.test.hb;

import lombok.AccessLevel;
import lombok.Getter;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealsPackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class HibernateTestItemMapper<TypeExchangeDeal> {
    @Getter(AccessLevel.PROTECTED)
    private final HibernateTestItem<TypeExchangeDeal> testItem;

    protected HibernateTestItemMapper(HibernateTestItem<TypeExchangeDeal> testItem) {
        this.testItem = testItem;
    }

    protected  <TypeSource, TypeTarget> Collection<TypeTarget> parse(Collection<TypeSource> source, Function<TypeSource, TypeTarget> function) {
        return source.stream().map(function).collect(Collectors.toList());
    }

    public Collection<TypeExchangeDeal> parse(ExchangeDealsPackage exchangeDealsPackage) {
        var deals = new ArrayList<TypeExchangeDeal>();

        for (ExchangeDealSource exchangeDealSource : exchangeDealsPackage.getObjects()) {
            deals.add(parse(exchangeDealSource));
        }

        return deals;
    }

    protected abstract TypeExchangeDeal parse(ExchangeDealSource exchangeDealSource);
}

