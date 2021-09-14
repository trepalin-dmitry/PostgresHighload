package pg.hl.test.hb.resolver.common;

import org.hibernate.Session;
import pg.hl.DevException;
import pg.hl.test.ResolveStrategy;
import pg.hl.test.hb.HibernateTestItemResolverParameters;

import java.util.HashMap;
import java.util.Map;

public abstract class CommonEntityResolver<TEntity, TKeyInternal, TKeyExternal> {

    private final ResolveStrategy resolveStrategy;
    private final Session session;
    private final Map<TKeyExternal, TEntity> cache = new HashMap<>();
    private final HibernateTestItemResolverParameters<TEntity, TKeyInternal, TKeyExternal> parameters;

    public CommonEntityResolver(ResolveStrategy resolveStrategy, Session session) {
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
            var item = parameters.getEntityClazz().cast(o);
            cache.put(parameters.getGetKeyExternalFunction().apply(item), item);
        }
    }

    public void cleanupCache() {
        switch (resolveStrategy) {
            case Cache:
                return;
            case Database:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + resolveStrategy);
        }

        for (TEntity value : cache.values()) {
            session.detach(value);
        }

        cache.clear();
    }

    public TEntity resolve(TKeyExternal keyExternal) {
        TEntity result;

        switch (resolveStrategy) {
            case Cache:
                result = resolveFromCache(keyExternal);
                break;
            case Database:
                result = resolveFromDatabase(keyExternal);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + resolveStrategy);
        }

        if (result == null) {
            throw new DevException("Не удалось получить сущность! keyExternal = " + keyExternal);
        }

        return result;
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
            result = parameters.getEntityClazz().cast(o);
        }
        return result;
    }
}

