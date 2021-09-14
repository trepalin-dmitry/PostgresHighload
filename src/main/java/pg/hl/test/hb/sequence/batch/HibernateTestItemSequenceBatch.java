package pg.hl.test.hb.sequence.batch;

import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.hb.CreateHibernateTestItemArgument;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.HibernateTestItemMapper;

public class HibernateTestItemSequenceBatch extends HibernateTestItem<ExchangeDealsPackage, ExchangeDealSource, ExchangeDealSequenceBatch> {

    public HibernateTestItemSequenceBatch(CreateHibernateTestItemArgument argument) {
        super(ExchangeDealsPackage.class, ExchangeDealSequenceBatch.class, argument);
    }

    @Override
    protected HibernateTestItemMapper<ExchangeDealSource, ExchangeDealSequenceBatch> createMapper() {
        return new HibernateTestItemMapperSequenceBatch(this);
    }
}
