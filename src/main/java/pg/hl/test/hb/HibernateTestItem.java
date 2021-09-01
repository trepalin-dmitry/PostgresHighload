package pg.hl.test.hb;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Configuration;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.jpa.ExchangeDeal;
import pg.hl.jpa.ExchangeDealPerson;
import pg.hl.jpa.ExchangeDealStatus;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.ProxyException;
import ru.vtb.zf.common.data.naming.PhysicalNamingStrategyQuotedImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class HibernateTestItem extends AbstractTestItem {
    SessionFactory sessionFactory = null;

    private final ConnectionPoolType connectionPoolType;
    private final SaveStrategy saveStrategy;

    public HibernateTestItem(ConnectionPoolType connectionPoolType, SaveStrategy saveStrategy) {
        this.connectionPoolType = connectionPoolType;
        this.saveStrategy = saveStrategy;
    }

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws ProxyException {
        try (var userService = new ExchangeDealService(createSession())) {

            var deals = new ArrayList<ExchangeDeal>();

            for (ExchangeDealSource exchangeDealSource : exchangeDealsPackage.getObjects()) {
                ExchangeDeal exchangeDeal = new ExchangeDeal();
                BeanUtils.copyProperties(exchangeDeal, exchangeDealSource);

                var persons = copyList(exchangeDealSource.getPersons(), ExchangeDealPerson.class);
                exchangeDeal.addPersonsAll(persons);

                var statuses = copyList(exchangeDealSource.getStatuses(), ExchangeDealStatus.class);
                exchangeDeal.addStatusesAll(statuses);

                deals.add(exchangeDeal);
            }

            userService.saveOrUpdateExchangeDeals(deals, saveStrategy);
        } catch (Exception e) {
            throw new ProxyException(e);
        }
    }

    protected <TypeFrom, TypeTo> List<TypeTo> copyList(List<TypeFrom> list, Class<TypeTo> clazz) {
        return list
                .stream()
                .map(itemFrom -> {
                    TypeTo itemTo;
                    try {
                        itemTo = clazz.getDeclaredConstructor().newInstance();
                        BeanUtils.copyProperties(itemTo, itemFrom);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return itemTo;
                })
                .collect(Collectors.toList());
    }

    public Session createSession() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure();
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

    public Collection<ExchangeDeal> find(int size) {
        try (var userService = new ExchangeDealService(createSession())) {
            return userService.find(size);
        }
    }
}

