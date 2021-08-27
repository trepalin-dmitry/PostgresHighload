package pg.hl.test.hb.simple;

import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Configuration;
import pg.hl.test.TestArgument;

public class HibernateC3p0TestItem extends HibernateCoreTestItem {
    public HibernateC3p0TestItem(TestArgument params) {
        super("Hibernate + C3p0", params);
    }

    @Override
    protected Configuration processConfiguration(Configuration configuration) {
        configuration.setProperty("hibernate.connection.provider_class", C3P0ConnectionProvider.class.getCanonicalName());
        configuration.setProperty("hibernate.c3p0.min_size", "5");
        configuration.setProperty("hibernate.c3p0.max_size", "20");
        configuration.setProperty("hibernate.c3p0.acquire_increment", "5");
        configuration.setProperty("hibernate.c3p0.timeout", "1800");
        return configuration;
    }
}
