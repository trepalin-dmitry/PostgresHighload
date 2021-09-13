package pg.hl.test.hb.sequence.batch;

import pg.hl.test.hb.CreateHibernateTestItemArgument;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.HibernateTestItemMapper;

import java.util.UUID;

public class HibernateTestItemSequenceBatch extends HibernateTestItem<ExchangeDealSequenceBatch> {

    public HibernateTestItemSequenceBatch(CreateHibernateTestItemArgument argument) {
        super(argument);
    }

    @Override
    protected String createExchangeDealHqlName() {
        return ExchangeDealSequenceBatch.class.getSimpleName();
    }

    @Override
    protected HibernateTestItemMapper<ExchangeDealSequenceBatch> createMapper() {
        return new HibernateTestItemMapperSequenceBatch(this);
    }

    @Override
    protected ExchangeDealSequenceBatch parse(Object o) {
        return (ExchangeDealSequenceBatch) o;
    }

    @Override
    protected Long getId(ExchangeDealSequenceBatch exchangeDealIdentity) {
        return exchangeDealIdentity.getId();
    }

    @Override
    protected UUID getGuid(ExchangeDealSequenceBatch exchangeDealIdentity) {
        return exchangeDealIdentity.getGuid();
    }

    @Override
    protected void setId(ExchangeDealSequenceBatch exchangeDealIdentity, Long id) {
        exchangeDealIdentity.setId(id);
    }
}
