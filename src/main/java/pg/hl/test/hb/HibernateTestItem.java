package pg.hl.test.hb;

import org.hibernate.HibernateException;
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
import pg.hl.test.AbstractTestItem;
import pg.hl.test.ProxyException;
import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.Person;
import pg.hl.test.hb.identity.ExchangeDealIdentity;
import pg.hl.test.hb.identity.ExchangeDealPersonIdentity;
import pg.hl.test.hb.identity.ExchangeDealStatusIdentity;
import pg.hl.test.hb.sequence.ExchangeDealPersonSequence;
import pg.hl.test.hb.sequence.ExchangeDealSequence;
import pg.hl.test.hb.sequence.ExchangeDealStatusSequence;
import ru.vtb.zf.common.data.naming.PhysicalNamingStrategyQuotedImpl;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class HibernateTestItem<TypeExchangeDeal> extends AbstractTestItem {
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
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws ProxyException {
        try {
            var exchangeDeals = mapper.parse(exchangeDealsPackage);
            saveOrUpdate(exchangeDeals, argument.getSaveStrategy(), argument.getCheckExistsStrategy());
        } catch (Throwable e) {
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
            configuration.setProperty("hibernate.show_sql", Boolean.toString(true));
            configuration.setProperty("hibernate.hbm2ddl.auto",
                    "create-drop"
//                    "validate"
            );

            configuration.setPhysicalNamingStrategy(new PhysicalNamingStrategyQuotedImpl()); // Через конфигурационный файл не работает (хотя инициализируется)

            configuration.addAnnotatedClass(Person.class);
            configuration.addAnnotatedClass(ExchangeDealStatusType.class);

            configuration.addAnnotatedClass(ExchangeDealSequence.class);
            configuration.addAnnotatedClass(ExchangeDealPersonSequence.class);
            configuration.addAnnotatedClass(ExchangeDealStatusSequence.class);

            configuration.addAnnotatedClass(ExchangeDealIdentity.class);
            configuration.addAnnotatedClass(ExchangeDealPersonIdentity.class);
            configuration.addAnnotatedClass(ExchangeDealStatusIdentity.class);

            switch (argument.getConnectionPoolType()) {
                case Hikari:
                    configuration.setProperty("hibernate.connection.provider_class", HikariCPConnectionProvider.class.getCanonicalName());
                    break;
                case C3p0:
                    configuration.setProperty("hibernate.connection.provider_class", C3P0ConnectionProvider.class.getCanonicalName());
                    configuration.setProperty("hibernate.c3p0.min_size", "5");
                    configuration.setProperty("hibernate.c3p0.max_size", Integer.toString(Integer.MAX_VALUE));
                    configuration.setProperty("hibernate.c3p0.acquire_increment", "5");
                    configuration.setProperty("hibernate.c3p0.timeout", "1800");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + argument.getConnectionPoolType());
            }

            switch (argument.getSaveStrategy()) {
                case Each:
                    break;
                case Batch:
                    configuration.setProperty("hibernate.jdbc.batch_size", "1000");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + argument.getSaveStrategy());
            }

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        }
        return sessionFactory.openSession();
    }

    public Collection<TypeExchangeDeal> findDeals(int maxValue) {
        var query = session.createQuery("From " + exchangeDealHqlName);
        query.setMaxResults(maxValue);
        List<TypeExchangeDeal> result = new ArrayList<>();
        for (Object o : query.list()) {
            result.add(parse(o));
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
        } catch (Exception exception) {
            transaction.rollback();
        }
    }

    public void saveOrUpdate(Collection<TypeExchangeDeal> exchangeDeals, SaveStrategy saveStrategy, CheckExistsStrategy checkExistsStrategy) {
        SaveConsumer<TypeExchangeDeal> saveConsumer;

        switch (saveStrategy) {
            case Batch:
                saveConsumer = this::saveOrUpdateInternalBatch;
                break;
            case Each:
                saveConsumer = this::saveOrUpdateInternalEach;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + saveStrategy);
        }

        saveOrUpdate(exchangeDeals, checkExistsStrategy, saveConsumer);
    }

    private void saveOrUpdate(Collection<TypeExchangeDeal> exchangeDeals, CheckExistsStrategy checkExistsStrategy, SaveConsumer<TypeExchangeDeal> consumer) {
        switch (checkExistsStrategy) {
            case Before:
                consumer.accept(new SaveConsumerArgument<>(exchangeDeals, true));
                break;
            case OnException:
                try {
                    consumer.accept(new SaveConsumerArgument<>(exchangeDeals, false));
                } catch (PersistenceException e) {
                    if (e.getCause() instanceof ConstraintViolationException) {
                        consumer.accept(new SaveConsumerArgument<>(exchangeDeals, true));
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + checkExistsStrategy);
        }
    }

    private Map<UUID, Long> createExistsMap(SaveConsumerArgument<TypeExchangeDeal> argument) {
        var existsKeys = new HashMap<UUID, Long>();

        if (argument.getCheckExist()) {
            // Получаем имеющиеся элементы
            var uuids = argument.getItems().stream().map(this::getGuid).collect(Collectors.toList());
            Query query = session.createQuery("SELECT e FROM " + exchangeDealHqlName + " e where e.guid in (:guids)").setParameterList("guids", uuids);
            for (Object o : query.getResultList()) {
                TypeExchangeDeal exchangeDeal = parse(o);
                existsKeys.put(getGuid(exchangeDeal), getId(exchangeDeal));
            }
        }

        return existsKeys;
    }

    protected abstract Long getId(TypeExchangeDeal exchangeDeal);

    protected abstract UUID getGuid(TypeExchangeDeal exchangeDeal);

    protected void saveOrUpdateInternalBatch(SaveConsumerArgument<TypeExchangeDeal> argument) throws HibernateException {
        var existsMap = createExistsMap(argument);
        sessionDoWithTransaction(session -> {
            for (TypeExchangeDeal exchangeDeal : argument.getItems()) {
                Long id = existsMap.get(getGuid(exchangeDeal));
                setId(exchangeDeal, id);
                if (id == null) {
                    session.persist(exchangeDeal);
                } else {
                    session.update(exchangeDeal);
                }
            }
            session.flush();
        });
    }

    protected abstract void setId(TypeExchangeDeal exchangeDeal, Long id);

    protected void saveOrUpdateInternalEach(SaveConsumerArgument<TypeExchangeDeal> argument) throws HibernateException {
        var existsMap = createExistsMap(argument);
        sessionDoWithTransaction(session -> {
            for (TypeExchangeDeal exchangeDeal : argument.getItems()) {
                var id = existsMap.get(getGuid(exchangeDeal));
                if (id != null) {
                    setId(exchangeDeal, id);
                    session.merge(exchangeDeal);
                } else {
                    session.update(exchangeDeal);
                }
                session.flush();
            }
        });
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
}

