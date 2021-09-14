package pg.hl.test.hb.resolver.common;

import org.hibernate.Session;
import pg.hl.test.ResolveStrategy;
import pg.hl.test.hb.HibernateTestItemResolverParameters;
import pg.hl.test.hb.common.ExchangeDealType;

public class ExchangeDealTypeResolver extends CommonEntityResolver<ExchangeDealType, Integer, String> {
    public ExchangeDealTypeResolver(ResolveStrategy resolveStrategy, Session session) {
        super(resolveStrategy, session);
    }

    @Override
    protected HibernateTestItemResolverParameters<ExchangeDealType, Integer, String> createParameters() {
        return new HibernateTestItemResolverParameters<ExchangeDealType, Integer, String>()
                .setGetKeyExternalFunction(ExchangeDealType::getCode)
                .setEntityClazz(ExchangeDealType.class)
                .setHqlAll("SELECT c FROM ExchangeDealType c")
                .setHqlByKeyExternal("SELECT c FROM ExchangeDealType c WHERE c.code = :code")
                .setHqlByKeyExternalParameterName("code");
    }
}