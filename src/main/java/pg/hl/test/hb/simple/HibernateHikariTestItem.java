package pg.hl.test.hb.simple;

import org.hibernate.cfg.Configuration;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;

public class HibernateHikariTestItem extends HibernateCoreTestItem {
    @Override
    protected Configuration processConfiguration(Configuration configuration) {
        configuration.setProperty("hibernate.connection.provider_class", HikariCPConnectionProvider.class.getCanonicalName());
        return configuration;
    }
}
