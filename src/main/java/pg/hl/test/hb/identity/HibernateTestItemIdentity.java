package pg.hl.test.hb.identity;

import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.hb.CreateHibernateTestItemArgument;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.HibernateTestItemMapper;

public class HibernateTestItemIdentity extends HibernateTestItem<ExchangeDealsPackage, ExchangeDealSource, ExchangeDealIdentity> {
    public HibernateTestItemIdentity(CreateHibernateTestItemArgument argument) {
        super(ExchangeDealsPackage.class, ExchangeDealIdentity.class, argument);
    }

    @Override
    protected HibernateTestItemMapper<ExchangeDealSource, ExchangeDealIdentity> createMapper() {
        return new HibernateTestItemMapperIdentity(this);
    }
}

