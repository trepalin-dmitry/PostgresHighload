package pg.hl.test.hb.sequence.one;

import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.hb.CreateHibernateTestItemArgument;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.HibernateTestItemMapper;

public class HibernateTestItemSequenceOne extends HibernateTestItem<ExchangeDealsPackage, ExchangeDealSource, ExchangeDealSequenceOne> {
    public HibernateTestItemSequenceOne(CreateHibernateTestItemArgument argument) {
        super(ExchangeDealsPackage.class, ExchangeDealSequenceOne.class, argument);
    }

    @Override
    protected HibernateTestItemMapper<ExchangeDealSource, ExchangeDealSequenceOne> createMapper() {
        return new HibernateTestItemMapperSequenceOne(this);
    }
}
