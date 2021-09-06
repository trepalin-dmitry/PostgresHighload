package pg.hl.test.hb;

import org.hibernate.Session;
import pg.hl.test.ResolveStrategy;
import pg.hl.test.hb.jpa.Person;

import java.util.UUID;

public class PersonResolver extends HibernateTestItemResolver<Person, Integer, UUID> {

    public PersonResolver(ResolveStrategy resolveStrategy, Session session) {
        super(resolveStrategy, session);
    }

    @Override
    protected HibernateTestItemResolverParameters<Person, Integer, UUID> createParameters() {
        return new PersonResolverParameters()
                .setGetKeyExternalFunction(Person::getGuid)
                .setParseObjectFunction(o -> (Person) o)
                .setHqlAll("SELECT c FROM Person c")
                .setHqlByKeyExternal("SELECT c FROM Person c WHERE c.guid = :guid")
                .setHqlByKeyExternalParameterName("guid");
    }


    private static class PersonResolverParameters
            extends HibernateTestItemResolverParameters<Person, Integer, UUID> {
    }
}
