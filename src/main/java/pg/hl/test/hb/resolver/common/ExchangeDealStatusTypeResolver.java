package pg.hl.test.hb.resolver.common;

import org.hibernate.Session;
import pg.hl.test.ResolveStrategy;
import pg.hl.test.hb.HibernateTestItemResolverParameters;
import pg.hl.test.hb.common.ExchangeDealStatusType;

public class ExchangeDealStatusTypeResolver extends CommonEntityResolver<ExchangeDealStatusType, Character, String> {

    public ExchangeDealStatusTypeResolver(ResolveStrategy resolveStrategy, Session session) {
        super(resolveStrategy, session);
    }

    @Override
    protected HibernateTestItemResolverParameters<ExchangeDealStatusType, Character, String> createParameters() {
        return new HibernateTestItemResolverParameters<ExchangeDealStatusType, Character, String>()
                .setGetKeyExternalFunction(ExchangeDealStatusType::getCode)
                .setEntityClazz(ExchangeDealStatusType.class)
                .setHqlAll("SELECT c FROM ExchangeDealStatusType c")
                .setHqlByKeyExternal("SELECT c FROM ExchangeDealStatusType c WHERE c.code = :code")
                .setHqlByKeyExternalParameterName("code");
    }
}

