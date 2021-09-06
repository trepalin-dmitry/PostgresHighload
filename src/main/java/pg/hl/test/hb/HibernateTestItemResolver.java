package pg.hl.test.hb;

import org.hibernate.Session;
import pg.hl.test.ResolveStrategy;

import java.util.HashMap;
import java.util.Map;

public abstract class HibernateTestItemResolver<TEntity, TKeyInternal, TKeyExternal> {

    private final ResolveStrategy resolveStrategy;
    private final Session session;
    private final Map<TKeyExternal, TEntity> cache = new HashMap<>();
    private final HibernateTestItemResolverParameters<TEntity, TKeyInternal, TKeyExternal> parameters;

    public HibernateTestItemResolver(ResolveStrategy resolveStrategy, Session session) {
        this.resolveStrategy = resolveStrategy;
        this.session = session;
        this.parameters = createParameters();
    }

    protected abstract HibernateTestItemResolverParameters<TEntity, TKeyInternal, TKeyExternal> createParameters();


    public void initCache() {
        switch (resolveStrategy) {
            case Cache:
                break;
            case Database:
                return;
            default:
                throw new IllegalStateException("Unexpected value: " + resolveStrategy);
        }

        var query = session.createQuery(parameters.getHqlAll());
        for (Object o : query.list()) {
            var item = parameters.getParseObjectFunction().apply(o);
            cache.put(parameters.getGetKeyExternalFunction().apply(item), item);
        }
    }

    public TEntity resolve(TKeyExternal keyExternal) {
        switch (resolveStrategy) {
            case Cache:
                return resolveFromCache(keyExternal);
            case Database:
                return resolveFromDatabase(keyExternal);
            default:
                throw new IllegalStateException("Unexpected value: " + resolveStrategy);
        }
    }

    private TEntity resolveFromCache(TKeyExternal keyExternal) {
        return cache.get(keyExternal);
    }

    private TEntity resolveFromDatabase(TKeyExternal keyExternal) {
        var query = session
                .createQuery(parameters.getHqlByKeyExternal())
                .setMaxResults(1)
                .setParameter(parameters.getHqlByKeyExternalParameterName(), keyExternal);
        TEntity result = null;
        for (Object o : query.list()) {
            result = parameters.getParseObjectFunction().apply(o);
        }
        return result;
    }
}

