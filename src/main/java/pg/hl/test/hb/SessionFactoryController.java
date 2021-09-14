package pg.hl.test.hb;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.postgresql.Driver;
import pg.hl.Settings;
import pg.hl.test.ConnectionPoolType;
import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.ExchangeDealType;
import pg.hl.test.hb.common.Person;
import pg.hl.test.hb.entity.multi.identity.ExchangeDealIdentity;
import pg.hl.test.hb.entity.multi.identity.ExchangeDealPersonIdentity;
import pg.hl.test.hb.entity.multi.identity.ExchangeDealStatusIdentity;
import pg.hl.test.hb.entity.multi.sequence.batch.ExchangeDealPersonSequenceBatch;
import pg.hl.test.hb.entity.multi.sequence.batch.ExchangeDealSequenceBatch;
import pg.hl.test.hb.entity.multi.sequence.batch.ExchangeDealStatusSequenceBatch;
import pg.hl.test.hb.entity.multi.sequence.one.ExchangeDealPersonSequenceOne;
import pg.hl.test.hb.entity.multi.sequence.one.ExchangeDealSequenceOne;
import pg.hl.test.hb.entity.multi.sequence.one.ExchangeDealStatusSequenceOne;
import pg.hl.test.hb.entity.simple.identity.SimpleExchangeDealIdentity;
import pg.hl.test.hb.entity.simple.sequence.batch.SimpleExchangeDealSequenceBatch;
import pg.hl.test.hb.entity.simple.sequence.one.SimpleExchangeDealSequenceOne;
import ru.vtb.zf.common.data.naming.PhysicalNamingStrategyQuotedImpl;

import java.util.HashMap;
import java.util.Map;

public class SessionFactoryController {
    private static final Map<SessionFactoryKey, SessionFactory> sessionFactoryMap = new HashMap<>();

    public static Session openSession(CreateHibernateTestItemArgument argument) {
        var sessionFactoryKey = new SessionFactoryKey(argument.getParentArgument().getConnectionPoolType(), argument.getBatchSize());
        var sessionFactory = sessionFactoryMap.get(sessionFactoryKey);
        if (sessionFactory == null) {
            sessionFactory = createSessionFactory(sessionFactoryKey);
            sessionFactoryMap.put(sessionFactoryKey, sessionFactory);
        }
        return sessionFactory.openSession();
    }

    private static SessionFactory createSessionFactory(SessionFactoryKey argument) {

        Configuration configuration = new Configuration();

        configuration.setProperty("hibernate.dialect", PostgreSQL95Dialect.class.getCanonicalName());
        configuration.setProperty("hibernate.connection.driver_class", Driver.class.getCanonicalName());
        configuration.setProperty("hibernate.connection.url", Settings.ConnectionSettings.JDBC_URL);
        configuration.setProperty("hibernate.connection.username", Settings.ConnectionSettings.USER);
        configuration.setProperty("hibernate.connection.password", Settings.ConnectionSettings.PASSWORD);
        configuration.setProperty("hibernate.connection.pool_size", Integer.toString(1));
        configuration.setProperty("hibernate.show_sql", Boolean.toString(Settings.Hibernate.SHOW_SQL));
        configuration.setProperty("hibernate.hbm2ddl.auto", Settings.Hibernate.HBM_2_DDL_AUTO.getValue());

        configuration.setPhysicalNamingStrategy(new PhysicalNamingStrategyQuotedImpl()); // Через конфигурационный файл не работает (хотя инициализируется)

        configuration.addAnnotatedClass(Person.class);
        configuration.addAnnotatedClass(ExchangeDealStatusType.class);
        configuration.addAnnotatedClass(ExchangeDealType.class);

        configuration.addAnnotatedClass(ExchangeDealSequenceOne.class);
        configuration.addAnnotatedClass(ExchangeDealPersonSequenceOne.class);
        configuration.addAnnotatedClass(ExchangeDealStatusSequenceOne.class);

        configuration.addAnnotatedClass(ExchangeDealSequenceBatch.class);
        configuration.addAnnotatedClass(ExchangeDealPersonSequenceBatch.class);
        configuration.addAnnotatedClass(ExchangeDealStatusSequenceBatch.class);

        configuration.addAnnotatedClass(ExchangeDealIdentity.class);
        configuration.addAnnotatedClass(ExchangeDealPersonIdentity.class);
        configuration.addAnnotatedClass(ExchangeDealStatusIdentity.class);

        configuration.addAnnotatedClass(SimpleExchangeDealSequenceOne.class);
        configuration.addAnnotatedClass(SimpleExchangeDealSequenceBatch.class);
        configuration.addAnnotatedClass(SimpleExchangeDealIdentity.class);

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

        configuration.setProperty("hibernate.jdbc.batch_size", String.valueOf(argument.getBatchSize().getValue()));

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        return configuration.buildSessionFactory(builder.build());
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    private static class SessionFactoryKey {
        private ConnectionPoolType connectionPoolType;
        private BatchSize batchSize;
    }
}
