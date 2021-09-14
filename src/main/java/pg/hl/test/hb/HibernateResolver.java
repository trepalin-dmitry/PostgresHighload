package pg.hl.test.hb;

import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.Person;

import java.util.UUID;

public interface HibernateResolver {
    ExchangeDealStatusType resolve(String code);

    Person resolve(UUID guid);
}
