package pg.hl.test.hb;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.postgresql.Driver;
import pg.hl.ExceptionsUtils;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.Person;
import pg.hl.test.hb.identity.ExchangeDealIdentity;
import pg.hl.test.hb.identity.ExchangeDealPersonIdentity;
import pg.hl.test.hb.identity.ExchangeDealStatusIdentity;
import pg.hl.test.hb.sequence.batch.ExchangeDealPersonSequenceBatch;
import pg.hl.test.hb.sequence.batch.ExchangeDealSequenceBatch;
import pg.hl.test.hb.sequence.batch.ExchangeDealStatusSequenceBatch;
import pg.hl.test.hb.sequence.one.ExchangeDealPersonSequenceOne;
import pg.hl.test.hb.sequence.one.ExchangeDealSequenceOne;
import pg.hl.test.hb.sequence.one.ExchangeDealStatusSequenceOne;
import ru.vtb.zf.common.data.naming.PhysicalNamingStrategyQuotedImpl;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class HibernateTestItem<TypeExchangeDeal> extends AbstractTestItem {
    private final static Hbm2DdlAuto HBM_2_DDL_AUTO = Hbm2DdlAuto.Validate;
    private final static Boolean SHOW_SQL = false;
    private SessionFactory sessionFactory = null;
    private final Session session;
    private final ExchangeDealStatusTypeResolver exchangeDealStatusTypeResolver;
    private final PersonResolver personResolver;
    private final HibernateTestItemMapper<TypeExchangeDeal> mapper;
    private final CreateHibernateTestItemArgument argument;
    private final String exchangeDealHqlName;

    public HibernateTestItem(CreateHibernateTestItemArgument argument) {
        this.argument = argument;
        this.exchangeDealHqlName = createExchangeDealHqlName();
        this.mapper = createMapper();
        this.session = createSessionInternal();
        this.exchangeDealStatusTypeResolver = new ExchangeDealStatusTypeResolver(this.argument.getParentArgument().getResolveStrategy(), session);
        this.personResolver = new PersonResolver(this.argument.getParentArgument().getResolveStrategy(), session);
        loadCache();
    }

    protected abstract String createExchangeDealHqlName();

    protected abstract HibernateTestItemMapper<TypeExchangeDeal> createMapper();

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) {
        var exchangeDeals = mapper.parse(exchangeDealsPackage);
        saveOrUpdate(exchangeDeals, argument.getCheckExistsStrategy());
    }

    protected Session createSessionInternal() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();

            configuration.setProperty("hibernate.dialect", PostgreSQL95Dialect.class.getCanonicalName());
            configuration.setProperty("hibernate.connection.driver_class", Driver.class.getCanonicalName());
            configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgresHighLoad");
            configuration.setProperty("hibernate.connection.username", "postgres");
            configuration.setProperty("hibernate.connection.password", "postgres");
            configuration.setProperty("hibernate.connection.pool_size", Integer.toString(1));
            configuration.setProperty("hibernate.show_sql", Boolean.toString(SHOW_SQL));
            configuration.setProperty("hibernate.hbm2ddl.auto", HBM_2_DDL_AUTO.getValue());

            configuration.setPhysicalNamingStrategy(new PhysicalNamingStrategyQuotedImpl()); // Через конфигурационный файл не работает (хотя инициализируется)

            configuration.addAnnotatedClass(Person.class);
            configuration.addAnnotatedClass(ExchangeDealStatusType.class);

            configuration.addAnnotatedClass(ExchangeDealSequenceOne.class);
            configuration.addAnnotatedClass(ExchangeDealPersonSequenceOne.class);
            configuration.addAnnotatedClass(ExchangeDealStatusSequenceOne.class);

            configuration.addAnnotatedClass(ExchangeDealSequenceBatch.class);
            configuration.addAnnotatedClass(ExchangeDealPersonSequenceBatch.class);
            configuration.addAnnotatedClass(ExchangeDealStatusSequenceBatch.class);

            configuration.addAnnotatedClass(ExchangeDealIdentity.class);
            configuration.addAnnotatedClass(ExchangeDealPersonIdentity.class);
            configuration.addAnnotatedClass(ExchangeDealStatusIdentity.class);

            switch (argument.getConnectionPoolType()) {
                case Hikari:
                    configuration.setProperty("hibernate.connection.provider_class", HikariCPConnectionProvider.class.getCanonicalName());
                    configuration.setProperty("hibernate.hikari.maximumPoolSize", Integer.toString(1));
                    break;
                case C3p0:
                    configuration.setProperty("hibernate.connection.provider_class", C3P0ConnectionProvider.class.getCanonicalName());
                    configuration.setProperty("hibernate.c3p0.min_size", Integer.toString(1));
                    configuration.setProperty("hibernate.c3p0.max_size", Integer.toString(1));
                    configuration.setProperty("hibernate.c3p0.acquire_increment", Integer.toString(1));
                    configuration.setProperty("hibernate.c3p0.timeout", "1800");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + argument.getConnectionPoolType());
            }

            configuration.setProperty("hibernate.jdbc.batch_size", String.valueOf(argument.getSaveStrategy().getValue()));

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        }
        return sessionFactory.openSession();
    }

    public Collection<UUID> findDealsGUIds(int maxValue) {
        var query = session.createQuery("From " + exchangeDealHqlName);
        query.setMaxResults(maxValue);
        List<UUID> result = new ArrayList<>();
        for (Object o : query.list()) {
            result.add(getGuid(parse(o)));
        }
        return result;
    }

    protected abstract TypeExchangeDeal parse(Object o);

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

    private void saveOrUpdate(Collection<TypeExchangeDeal> exchangeDeals, CheckExistsStrategy checkExistsStrategy) {
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

    protected void saveOrUpdateInternal(Collection<TypeExchangeDeal> items, Boolean checkExist, Boolean repeat) {
        try {
            sessionDoWithTransaction(session -> {
                var existsMap = new HashMap<UUID, Long>();

                if (checkExist) {
                    // Получаем имеющиеся элементы
                    var uuids = items.stream().map(this::getGuid).collect(Collectors.toList());
                    Query query = session.createQuery("SELECT e FROM " + exchangeDealHqlName + " e where e.guid in (:guids)").setParameterList("guids", uuids);
                    for (Object o : query.getResultList()) {
                        TypeExchangeDeal exchangeDeal = parse(o);
                        existsMap.put(getGuid(exchangeDeal), getId(exchangeDeal));
                    }
                }

                for (TypeExchangeDeal exchangeDeal : items) {
                    if (checkExist) {
                        Long id = existsMap.get(getGuid(exchangeDeal));
                        if (id != null) {
                            setId(exchangeDeal, id);
                            session.merge(exchangeDeal);
                            continue;
                        }
                    }

                    if (repeat){
                        session.save(exchangeDeal);
                    }
                    else {
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

    protected abstract void setId(TypeExchangeDeal exchangeDeal, Long id);

    protected abstract Long getId(TypeExchangeDeal exchangeDeal);

    protected abstract UUID getGuid(TypeExchangeDeal exchangeDeal);

    protected void cleanupCache() {
        exchangeDealStatusTypeResolver.cleanupCache();
        personResolver.cleanupCache();
    }
}