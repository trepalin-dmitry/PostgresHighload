package pg.hl.test.hb.simple;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import pg.hl.jpa.ExchangeDeal;
import pg.hl.jpa.ExchangeDealPerson;
import pg.hl.jpa.ExchangeDealStatus;
import ru.vtb.zf.common.data.naming.PhysicalNamingStrategyQuotedImpl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SessionFactoryUtils {
    private static final Map<Type, SessionFactory> sessionFactories = new HashMap<>();

    public static <TypeConnectionProvider extends ConnectionProvider> SessionFactory getSessionFactory(Class<TypeConnectionProvider> clazz) {
        synchronized (sessionFactories) {
            var sessionFactory = sessionFactories.get(clazz);
            if (sessionFactory == null) {
                Configuration configuration = new Configuration().configure();
                configuration.setProperty("hibernate.connection.provider_class", clazz.getCanonicalName());
                configuration.addAnnotatedClass(ExchangeDeal.class);
                configuration.addAnnotatedClass(ExchangeDealPerson.class);
                configuration.addAnnotatedClass(ExchangeDealStatus.class);
                configuration.setPhysicalNamingStrategy(new PhysicalNamingStrategyQuotedImpl()); // Через конфигурационный файл не работает (хотя инициализируется)
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
                sessionFactories.put(clazz, sessionFactory);
            }
            return sessionFactory;
        }
    }
}
