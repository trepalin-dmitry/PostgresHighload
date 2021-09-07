package pg.hl.test.hb.identity;

import pg.hl.test.hb.CreateHibernateTestItemArgument;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.HibernateTestItemMapper;

import java.util.UUID;

public class HibernateTestItemIdentity extends HibernateTestItem<ExchangeDealIdentity> {
    public HibernateTestItemIdentity(CreateHibernateTestItemArgument argument) {
        super(argument);
    }

    @Override
    protected String createExchangeDealHqlName() {
        return ExchangeDealIdentity.class.getSimpleName();
    }

    @Override
    protected HibernateTestItemMapper<ExchangeDealIdentity> createMapper() {
        return new HibernateTestItemMapperIdentity(this);
    }

    @Override
    protected ExchangeDealIdentity parse(Object o) {
        return (ExchangeDealIdentity) o;
    }

    @Override
    protected Long getId(ExchangeDealIdentity exchangeDealIdentity) {
        return exchangeDealIdentity.getId();
    }

    @Override
    protected UUID getGuid(ExchangeDealIdentity exchangeDealIdentity) {
        return exchangeDealIdentity.getGuid();
    }

    @Override
    protected void setId(ExchangeDealIdentity exchangeDealIdentity, Long id) {
        exchangeDealIdentity.setId(id);
    }
}

