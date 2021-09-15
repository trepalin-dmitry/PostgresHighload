package pg.hl.test.sp;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.Driver;
import pg.hl.config.Configuration;
import pg.hl.test.ConnectionPoolType;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionPoolController {
    private static final Map<ConnectionPoolType, DataSource> dataSourceMap = new HashMap<>();

    public static Connection getConnection(ConnectionPoolType type) throws PropertyVetoException, SQLException, IOException {
        var dataSource = dataSourceMap.get(type);
        if (dataSource == null){
            dataSource = createDataSource(type);
            dataSourceMap.put(type, dataSource);
        }
        return dataSource.getConnection();
    }

    private static DataSource createDataSource(ConnectionPoolType type) throws PropertyVetoException, IOException {
        switch (type){
            case Hikari:
                return createHikariDataSource();
            case C3p0:
                return createC3p0DataSource();
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private static DataSource createHikariDataSource() throws IOException {
        var dataSource = new HikariDataSource();
        dataSource.setDriverClassName(Driver.class.getCanonicalName());
        dataSource.setUsername(Configuration.getInstance().getConnection().getUser());
        dataSource.setPassword(Configuration.getInstance().getConnection().getPassword());
        dataSource.setJdbcUrl(Configuration.getInstance().getConnection().getJdbcUrl());
        return dataSource;
    }

    private static DataSource createC3p0DataSource() throws PropertyVetoException, IOException {
        var dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(Driver.class.getCanonicalName());
        dataSource.setUser(Configuration.getInstance().getConnection().getUser());
        dataSource.setPassword(Configuration.getInstance().getConnection().getPassword());
        dataSource.setJdbcUrl(Configuration.getInstance().getConnection().getJdbcUrl());
        return dataSource;
    }
}
