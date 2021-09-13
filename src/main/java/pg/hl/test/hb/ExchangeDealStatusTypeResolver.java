package pg.hl.test.hb;

import org.hibernate.Session;
import pg.hl.test.ResolveStrategy;
import pg.hl.test.hb.common.ExchangeDealStatusType;

public class ExchangeDealStatusTypeResolver extends HibernateTestItemResolver<ExchangeDealStatusType, Character, String> {

    public ExchangeDealStatusTypeResolver(ResolveStrategy resolveStrategy, Session session) {
        super(resolveStrategy, session);
    }

    @Override
    protected HibernateTestItemResolverParameters<ExchangeDealStatusType, Character, String> createParameters() {
        return new ExchangeDealStatusTypeResolverParameters()
                .setGetKeyExternalFunction(ExchangeDealStatusType::getCode)
                .setParseObjectFunction(o -> (ExchangeDealStatusType) o)
                .setHqlAll("SELECT c FROM ExchangeDealStatusType c")
                .setHqlByKeyExternal("SELECT c FROM ExchangeDealStatusType c WHERE c.code = :code")
                .setHqlByKeyExternalParameterName("code");
    }

    private static class ExchangeDealStatusTypeResolverParameters
            extends HibernateTestItemResolverParameters<ExchangeDealStatusType, Character, String> {
    }
}

