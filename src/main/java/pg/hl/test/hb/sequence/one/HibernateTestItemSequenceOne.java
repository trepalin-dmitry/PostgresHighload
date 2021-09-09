package pg.hl.test.hb.sequence.one;

import pg.hl.test.hb.CreateHibernateTestItemArgument;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.HibernateTestItemMapper;

import java.util.UUID;

public class HibernateTestItemSequenceOne extends HibernateTestItem<ExchangeDealSequenceOne> {

    public HibernateTestItemSequenceOne(CreateHibernateTestItemArgument argument) {
        super(argument);
    }

    @Override
    protected String createExchangeDealHqlName() {
        return ExchangeDealSequenceOne.class.getSimpleName();
    }

    @Override
    protected HibernateTestItemMapper<ExchangeDealSequenceOne> createMapper() {
        return new HibernateTestItemMapperSequenceOne(this);
    }

    @Override
    protected ExchangeDealSequenceOne parse(Object o) {
        return (ExchangeDealSequenceOne) o;
    }

    @Override
    protected Long getId(ExchangeDealSequenceOne exchangeDealIdentity) {
        return exchangeDealIdentity.getId();
    }

    @Override
    protected UUID getGuid(ExchangeDealSequenceOne exchangeDealIdentity) {
        return exchangeDealIdentity.getGuid();
    }

    @Override
    protected void setId(ExchangeDealSequenceOne exchangeDealIdentity, Long id) {
        exchangeDealIdentity.setId(id);
    }
}
