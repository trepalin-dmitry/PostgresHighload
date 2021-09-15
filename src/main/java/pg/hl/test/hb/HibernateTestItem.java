package pg.hl.test.hb;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import pg.hl.DevException;
import pg.hl.ExceptionsUtils;
import pg.hl.dto.AbstractDataObject;
import pg.hl.dto.AbstractDataPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.DatabaseHelper;
import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.ExchangeDealType;
import pg.hl.test.hb.common.Person;
import pg.hl.test.hb.resolver.common.ExchangeDealStatusTypeResolver;
import pg.hl.test.hb.resolver.common.ExchangeDealTypeResolver;
import pg.hl.test.hb.resolver.common.PersonResolver;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class HibernateTestItem<
        TypePackage extends AbstractDataPackage<TypeExchangeDealSource>,
        TypeExchangeDealSource extends AbstractDataObject,
        TypeExchangeDealTarget extends HibernateRootEntity,
        TypeMapper extends HibernateTestItemMapper<TypeExchangeDealSource, TypeExchangeDealTarget>
        > extends AbstractTestItem<TypePackage, TypeExchangeDealSource> implements HibernateResolver, DatabaseHelper {

    private final TypeMapper mapper;
    private final Session session;
    private final ExchangeDealStatusTypeResolver exchangeDealStatusTypeResolver;
    private final PersonResolver personResolver;
    private final ExchangeDealTypeResolver exchangeDealTypeResolver;
    private final CreateHibernateTestItemArgument argument;
    private final String exchangeDealHqlName;
    private final Class<TypeExchangeDealTarget> typeExchangeDealTargetClazz;

    public HibernateTestItem(Class<TypePackage> typePackageClazz, Class<TypeExchangeDealTarget> typeExchangeDealTargetClazz, Class<TypeMapper> typeMapperClazz, CreateHibernateTestItemArgument argument) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        super(argument.getParentArgument(), typePackageClazz);
        this.typeExchangeDealTargetClazz = typeExchangeDealTargetClazz;
        this.argument = argument;
        this.exchangeDealHqlName = createExchangeDealHqlName();
        this.mapper = typeMapperClazz.getConstructor(HibernateResolver.class).newInstance(this);
        this.session = createSessionInternal();
        this.exchangeDealStatusTypeResolver = new ExchangeDealStatusTypeResolver(this.argument.getParentArgument().getResolveStrategy(), session);
        this.personResolver = new PersonResolver(this.argument.getParentArgument().getResolveStrategy(), session);
        this.exchangeDealTypeResolver = new ExchangeDealTypeResolver(this.argument.getParentArgument().getResolveStrategy(), session);
        loadCache();
    }

    private String createExchangeDealHqlName() {
        return typeExchangeDealTargetClazz.getSimpleName();
    }

    @Override
    protected void uploadDeals(TypePackage dealsPackage) {
        saveOrUpdate(mapper.parse(dealsPackage), argument.getCheckExistsStrategy());
    }

    protected Session createSessionInternal() throws IOException {
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

    private TypeExchangeDealTarget cast(Object o) {
        return typeExchangeDealTargetClazz.cast(o);
    }

    public Collection<Person> findPersons(int maxValue) {
        return findInternal(Person.class, "From Person", maxValue);
    }

    public Collection<ExchangeDealStatusType> findStatusesTypes(int maxValue) {
        return findInternal(ExchangeDealStatusType.class, "From ExchangeDealStatusType", maxValue);
    }

    public Collection<ExchangeDealType> findDealsTypes(int maxValue) {
        return findInternal(ExchangeDealType.class, "From ExchangeDealType", maxValue);
    }

    public <TEntity> List<TEntity> findInternal(Class<TEntity> clazz, String queryString, int maxValue) {
        List<TEntity> result = new ArrayList<>();
        for (Object o : session.createQuery(queryString).setMaxResults(maxValue).list()) {
            result.add(clazz.cast(o));
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

                    if (repeat && existsMap.size() == 0){
                        throw new DevException("Значения е найдены!");
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

    public ExchangeDealType resolveDealType(String code) {
        return exchangeDealTypeResolver.resolve(code);
    }

    public ExchangeDealStatusType resolveStatusType(String code) {
        return exchangeDealStatusTypeResolver.resolve(code);
    }

    public Person resolvePerson(UUID guid) {
        return personResolver.resolve(guid);
    }

    private void loadCache() {
        exchangeDealTypeResolver.initCache();
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
        exchangeDealTypeResolver.cleanupCache();
        exchangeDealStatusTypeResolver.cleanupCache();
        personResolver.cleanupCache();
    }

    @Override
    public void close() throws SQLException {
        session.close();

        super.close();
    }
}

