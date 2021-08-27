package pg.hl.test.hb.simple;

import org.hibernate.cfg.Configuration;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import pg.hl.test.TestArgument;

public class HibernateHikariTestItem extends HibernateCoreTestItem {
    public HibernateHikariTestItem(TestArgument params) {
        super("Hibernate + Hikari", params);
    }

    @Override
    protected Configuration processConfiguration(Configuration configuration) {
        configuration.setProperty("hibernate.connection.provider_class", HikariCPConnectionProvider.class.getCanonicalName());
        return configuration;
    }
}
