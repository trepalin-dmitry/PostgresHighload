package pg.hl.test.hb;

import lombok.AccessLevel;
import lombok.Getter;
import pg.hl.dto.AbstractDataObject;
import pg.hl.dto.AbstractDataPackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class HibernateTestItemMapper<TypeExchangeDealSource extends AbstractDataObject, TypeExchangeDealTarget> {
    @Getter(AccessLevel.PROTECTED)
    private final HibernateResolver resolver;

    protected HibernateTestItemMapper(HibernateResolver resolver) {
        this.resolver = resolver;
    }

    protected <TypeSource, TypeTarget> Collection<TypeTarget> parse(Collection<TypeSource> source, Function<TypeSource, TypeTarget> function) {
        return source
                .stream()
                .map(function)
                .collect(Collectors.toList());
    }

    public <TypeExchangeDealSourcePackage extends AbstractDataPackage<TypeExchangeDealSource>> Collection<TypeExchangeDealTarget> parse(TypeExchangeDealSourcePackage exchangeDealsPackage) {
        var deals = new ArrayList<TypeExchangeDealTarget>();

        for (TypeExchangeDealSource exchangeDealSource : exchangeDealsPackage.getObjects()) {
            deals.add(parse(exchangeDealSource));
        }

        return deals;
    }

    protected abstract TypeExchangeDealTarget parse(TypeExchangeDealSource exchangeDealSource);
}

