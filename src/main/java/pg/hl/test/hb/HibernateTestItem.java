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
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.ResolveStrategy;
import pg.hl.test.hb.jpa.*;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.ProxyException;
import ru.vtb.zf.common.data.naming.PhysicalNamingStrategyQuotedImpl;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HibernateTestItem extends AbstractTestItem {
    SessionFactory sessionFactory = null;
    private final ConnectionPoolType connectionPoolType;
    private final SaveStrategy saveStrategy;
    private final HibernateTestItemMapper mapper;
    private final Session session;
    private final ExchangeDealStatusTypeResolver exchangeDealStatusTypeResolver;
    private final PersonResolver personResolver;

    public HibernateTestItem(ResolveStrategy resolveStrategy, ConnectionPoolType connectionPoolType, SaveStrategy saveStrategy) {
        this.connectionPoolType = connectionPoolType;
        this.saveStrategy = saveStrategy;
        this.mapper = new HibernateTestItemMapper(this);
        this.session = createSessionInternal();
        this.exchangeDealStatusTypeResolver = new ExchangeDealStatusTypeResolver(resolveStrategy, session);
        this.personResolver = new PersonResolver(resolveStrategy, session);
        loadCache();
    }

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws ProxyException {
        try {
            var exchangeDeals = mapper.parse(exchangeDealsPackage);
            saveOrUpdate(exchangeDeals, saveStrategy);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ProxyException(e);
        }
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
            configuration.setProperty("hibernate.show_sql", Boolean.toString(false));
            configuration.setProperty("hibernate.hbm2ddl.auto",
                    //"create-drop"
                    "validate"
            );

            configuration.addAnnotatedClass(Person.class);
            configuration.addAnnotatedClass(ExchangeDealStatusType.class);
            configuration.addAnnotatedClass(ExchangeDeal.class);
            configuration.addAnnotatedClass(ExchangeDealPerson.class);
            configuration.addAnnotatedClass(ExchangeDealStatus.class);

            configuration.setPhysicalNamingStrategy(new PhysicalNamingStrategyQuotedImpl()); // Через конфигурационный файл не работает (хотя инициализируется)

            switch (connectionPoolType) {
                case Hikari:
                    configuration.setProperty("hibernate.connection.provider_class", HikariCPConnectionProvider.class.getCanonicalName());
                    break;
                case C3p0:
                    configuration.setProperty("hibernate.connection.provider_class", C3P0ConnectionProvider.class.getCanonicalName());
                    configuration.setProperty("hibernate.c3p0.min_size", "5");
                    configuration.setProperty("hibernate.c3p0.max_size", "20");
                    configuration.setProperty("hibernate.c3p0.acquire_increment", "5");
                    configuration.setProperty("hibernate.c3p0.timeout", "1800");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + connectionPoolType);
            }

            switch (saveStrategy) {
                case Each:
                    break;
                case BatchHandleException:
                case BatchCheckExistsBefore:
                    configuration.setProperty("hibernate.jdbc.batch_size", "1000");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + saveStrategy);
            }

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        }
        return sessionFactory.openSession();
    }

    public Collection<ExchangeDeal> findDeals(int maxValue) {
        var query = session.createQuery("From ExchangeDeal");
        query.setMaxResults(maxValue);
        List<ExchangeDeal> result = new ArrayList<>();
        for (Object o : query.list()) {
            result.add((ExchangeDeal) o);
        }
        return result;
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

    private void sessionDoWithTransaction(Consumer<Session> consumer) {
        Transaction transaction = session.beginTransaction();
        try {
            consumer.accept(session);
            transaction.commit();
        } catch (Exception exception) {
            transaction.rollback();
            throw exception;
        }
    }

    protected void saveOrUpdateInternalEach(Collection<ExchangeDeal> exchangeDeals) {
        sessionDoWithTransaction(session -> {
            for (ExchangeDeal deal : exchangeDeals) {
                session.saveOrUpdate(deal);
            }
        });
    }

    public void saveOrUpdate(Collection<ExchangeDeal> exchangeDeals, SaveStrategy useBatchMode) {
        switch (useBatchMode) {
            case BatchHandleException:
                try {
                    saveOrUpdateInternalBatch(exchangeDeals, false);
                } catch (ConstraintViolationException e) {
                    saveOrUpdateInternalBatch(exchangeDeals, true);
                }
                break;
            case BatchCheckExistsBefore:
                saveOrUpdateInternalBatch(exchangeDeals, true);
                break;
            case Each:
                saveOrUpdateInternalEach(exchangeDeals);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + useBatchMode);
        }
    }

    protected void saveOrUpdateInternalBatch(Collection<ExchangeDeal> exchangeDeals, boolean checkExists) throws ConstraintViolationException {
        var existsKeys = new HashSet<UUID>();

        if (checkExists) {
            // Получаем имеющиеся элементы
            var uuids = exchangeDeals.stream().map(ExchangeDeal::getGuid).collect(Collectors.toList());
            Query query = session.createQuery("SELECT e.guid FROM ExchangeDeal e where e.guid in (:guids)").setParameterList("guids", uuids);
            for (Object o : query.getResultList()) {
                existsKeys.add(UUID.fromString(o.toString()));
            }
        }

        var reference = new AtomicReference<ConstraintViolationException>();

        sessionDoWithTransaction(session -> {
            for (ExchangeDeal exchangeDeal : exchangeDeals) {
                if (existsKeys.contains(exchangeDeal.getGuid())) {
                    session.update(exchangeDeal);
                } else {
                    session.persist(exchangeDeal);
                }
            }

            try {
                session.flush();
            } catch (PersistenceException persistenceException) {
                var cause = persistenceException.getCause();
                if (cause instanceof ConstraintViolationException) {
                    reference.set((ConstraintViolationException) cause);
                }
            }
        });

        var exception = reference.get();
        if (exception != null) {
            throw exception;
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
}

