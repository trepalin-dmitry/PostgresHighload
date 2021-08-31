package pg.hl.test.hb;

import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Configuration;

public class HibernateC3p0TestItem extends HibernateCoreTestItem {
    public HibernateC3p0TestItem() {
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
