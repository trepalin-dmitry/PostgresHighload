package pg.hl.test.hb;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import pg.hl.ExceptionsUtils;
import pg.hl.dto.AbstractDataObject;
import pg.hl.dto.AbstractDataPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.Person;
import pg.hl.test.hb.identity.HibernateRootEntity;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class HibernateTestItem<
        TypePackage extends AbstractDataPackage<TypeExchangeDealSource>,
        TypeExchangeDealSource extends AbstractDataObject,
        TypeExchangeDealTarget extends HibernateRootEntity
        > extends AbstractTestItem<TypePackage, TypeExchangeDealSource> implements HibernateResolver {

    private final HibernateTestItemMapper<TypeExchangeDealSource, TypeExchangeDealTarget> mapper;
    private final Session session;
    private final ExchangeDealStatusTypeResolver exchangeDealStatusTypeResolver;
    private final PersonResolver personResolver;
    private final CreateHibernateTestItemArgument argument;
    private final String exchangeDealHqlName;
    private final Class<TypeExchangeDealTarget> typeExchangeDealTargetClazz;

    protected HibernateTestItem(Class<TypePackage> typePackageClazz, Class<TypeExchangeDealTarget> typeExchangeDealTargetClazz, CreateHibernateTestItemArgument argument) {
        super(argument.getParentArgument(), typePackageClazz);
        this.typeExchangeDealTargetClazz = typeExchangeDealTargetClazz;
        this.argument = argument;
        this.exchangeDealHqlName = createExchangeDealHqlName();
        this.mapper = createMapper();
        this.session = createSessionInternal();
        this.exchangeDealStatusTypeResolver = new ExchangeDealStatusTypeResolver(this.argument.getParentArgument().getResolveStrategy(), session);
        this.personResolver = new PersonResolver(this.argument.getParentArgument().getResolveStrategy(), session);
        loadCache();
    }

    private String createExchangeDealHqlName() {
        return typeExchangeDealTargetClazz.getSimpleName();
    }

    protected abstract HibernateTestItemMapper<TypeExchangeDealSource, TypeExchangeDealTarget> createMapper();

    @Override
    protected void uploadDeals(TypePackage dealsPackage) {
        saveOrUpdate(mapper.parse(dealsPackage), argument.getCheckExistsStrategy());
    }

    protected Session createSessionInternal() {
        return SessionFactoryController.openSession(argument);
    }

    public Collection<UUID> findDealsGUIds(int maxValue) {
        var query = session.createQuery("From " + exchangeDealHqlName);
        query.setMaxResults(maxValue);
        List<UUID> result = new ArrayList<>();
        for (Object o : query.list()) {
            result.add(cast(o).getGuid());
        }
        return result;
    }

    private TypeExchangeDealTarget cast(Object o){
        return typeExchangeDealTargetClazz.cast(o);
    }

    public Collection<Person> findPersons(int maxValue) {
        return findInternal("From Person", maxValue, o -> (Person) o);
    }

    public Collection<ExchangeDealStatusType> findStatusesTypes(int maxValue) {
        return findInternal("From ExchangeDealStatusType", maxValue, o -> (ExchangeDealStatusType) o);
    }

    public <TEntity> List<TEntity> findInternal(String queryString, int maxValue, Function<Object, TEntity> function) {
        List<TEntity> result = new ArrayList<>();
        for (Object o : session.createQuery(queryString).setMaxResults(maxValue).list()) {
            result.add(function.apply(o));
        }
        return result;
    }

    private void sessionDoWithTransaction(SessionConsumer consumer) {
        Transaction transaction = session.beginTransaction();
        try {
            consumer.accept(session);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    private void saveOrUpdate(Collection<TypeExchangeDealTarget> exchangeDeals, CheckExistsStrategy checkExistsStrategy) {
        switch (checkExistsStrategy) {
            case Before:
                saveOrUpdateInternal(exchangeDeals, true, false);
                break;
            case OnException:
                try {
                    saveOrUpdateInternal(exchangeDeals, false, false);
                } catch (PersistenceException e) {
                    if (ExceptionsUtils.findCause(ConstraintViolationException.class, e) != null) {
                        saveOrUpdateInternal(exchangeDeals, true, true);
                    } else {
                        throw e;
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + checkExistsStrategy);
        }
    }

    protected void saveOrUpdateInternal(Collection<TypeExchangeDealTarget> items, Boolean checkExist, Boolean repeat) {
        try {
            sessionDoWithTransaction(session -> {
                var existsMap = new HashMap<UUID, Long>();

                if (checkExist) {
                    // Получаем имеющиеся элементы
                    var uuids = items.stream().map(TypeExchangeDealTarget::getGuid).collect(Collectors.toList());
                    Query query = session.createQuery("SELECT e FROM " + exchangeDealHqlName + " e where e.guid in (:guids)").setParameterList("guids", uuids);
                    for (Object o : query.getResultList()) {
                        TypeExchangeDealTarget exchangeDeal = cast(o);
                        existsMap.put(exchangeDeal.getGuid(), exchangeDeal.getId());
                    }
                }

                for (TypeExchangeDealTarget exchangeDeal : items) {
                    if (checkExist) {
                        Long id = existsMap.get(exchangeDeal.getGuid());
                        if (id != null) {
                            exchangeDeal.setId(id);
                            session.merge(exchangeDeal);
                            continue;
                        }
                    }

                    if (repeat) {
                        session.save(exchangeDeal);
                    } else {
                        session.persist(exchangeDeal);
                    }
                }
            });
        } finally {
            cleanupCache();
        }
    }

    public ExchangeDealStatusType resolve(String code) {
        return exchangeDealStatusTypeResolver.resolve(code);
    }

    public Person resolve(UUID guid) {
        return personResolver.resolve(guid);
    }

    private void loadCache() {
        exchangeDealStatusTypeResolver.initCache();
        personResolver.initCache();
    }

    public <T> void save(Collection<T> items) {
        sessionDoWithTransaction(session -> {
            for (T item : items) {
                session.save(item);
            }
            session.flush();
        });
    }

    protected void cleanupCache() {
        exchangeDealStatusTypeResolver.cleanupCache();
        personResolver.cleanupCache();
    }

    @Override
    public void close() throws SQLException {
        session.close();

        super.close();
    }
}

