package pg.hl.test.hb.simple;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import pg.hl.jpa.ExchangeDeal;
import pg.hl.jpa.ExchangeDealPerson;
import pg.hl.jpa.ExchangeDealStatus;
import ru.vtb.zf.common.data.naming.PhysicalNamingStrategyQuotedImpl;

public class HibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtil() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(ExchangeDeal.class);
            configuration.addAnnotatedClass(ExchangeDealPerson.class);
            configuration.addAnnotatedClass(ExchangeDealStatus.class);
            configuration.setPhysicalNamingStrategy(new PhysicalNamingStrategyQuotedImpl()); // Через конфигурационный файл не работает (хотя инициализируется)
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        }
        return sessionFactory;
    }
}
