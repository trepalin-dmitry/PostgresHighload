package pg.hl.test.hb;

import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.ExchangeDealType;
import pg.hl.test.hb.common.Person;

import java.util.UUID;

public interface HibernateResolver {
    ExchangeDealType resolveDealType(String code);

    ExchangeDealStatusType resolveStatusType(String code);

    Person resolvePerson(UUID guid);
}
