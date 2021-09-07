package pg.hl.test.hb.sequence;

import pg.hl.test.hb.CreateHibernateTestItemArgument;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.HibernateTestItemMapper;

import java.util.UUID;

public class HibernateTestItemSequence extends HibernateTestItem<ExchangeDealSequence> {

    public HibernateTestItemSequence(CreateHibernateTestItemArgument argument) {
        super(argument);
    }

    @Override
    protected String createExchangeDealHqlName() {
        return ExchangeDealSequence.class.getSimpleName();
    }

    @Override
    protected HibernateTestItemMapper<ExchangeDealSequence> createMapper() {
        return new HibernateTestItemMapperSequence(this);
    }

    @Override
    protected ExchangeDealSequence parse(Object o) {
        return (ExchangeDealSequence) o;
    }

    @Override
    protected Long getId(ExchangeDealSequence exchangeDealIdentity) {
        return exchangeDealIdentity.getId();
    }

    @Override
    protected UUID getGuid(ExchangeDealSequence exchangeDealIdentity) {
        return exchangeDealIdentity.getGuid();
    }

    @Override
    protected void setId(ExchangeDealSequence exchangeDealIdentity, Long id) {
        exchangeDealIdentity.setId(id);
    }
}
