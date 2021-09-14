package pg.hl.test.hb.resolver.common;

import org.hibernate.Session;
import pg.hl.test.ResolveStrategy;
import pg.hl.test.hb.HibernateTestItemResolverParameters;
import pg.hl.test.hb.common.Person;

import java.util.UUID;

public class PersonResolver extends CommonEntityResolver<Person, Integer, UUID> {

    public PersonResolver(ResolveStrategy resolveStrategy, Session session) {
        super(resolveStrategy, session);
    }

    @Override
    protected HibernateTestItemResolverParameters<Person, Integer, UUID> createParameters() {
        return new HibernateTestItemResolverParameters<Person, Integer, UUID>()
                .setGetKeyExternalFunction(Person::getGuid)
                .setEntityClazz(Person.class)
                .setHqlAll("SELECT c FROM Person c")
                .setHqlByKeyExternal("SELECT c FROM Person c WHERE c.guid = :guid")
                .setHqlByKeyExternalParameterName("guid");
    }
}
