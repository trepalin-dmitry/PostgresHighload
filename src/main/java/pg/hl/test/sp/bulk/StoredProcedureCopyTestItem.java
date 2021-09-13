package pg.hl.test.sp.bulk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.postgresql.jdbc.PgConnection;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.CreateTestItemArgument;
import pg.hl.test.sp.ei.Mapper;

import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StoredProcedureCopyTestItem extends AbstractTestItem {
    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private final UploadFunction uploadFunction;

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private final CallableStatement statementBefore;
    private final CallableStatement statementAfter;

    public StoredProcedureCopyTestItem(CreateTestItemArgument argument) throws SQLException {
        var url = "jdbc:postgresql://localhost/postgresHighLoad?user=postgres&password=postgres";
        var connection = DriverManager.getConnection(url).unwrap(PgConnection.class);

        connection.prepareStatement("call \"exchangeDeals@CreateTables." + argument.getResolveStrategy() + "\"();").execute();
        this.statementBefore = connection.prepareCall("call \"exchangeDeals@CleanupTables." + argument.getResolveStrategy() + "\"();");
        this.statementAfter = connection.prepareCall("call \"exchangeDeals@SaveCopy." + argument.getResolveStrategy() + "." + argument.getIdentityStrategy() + "\"(?);");

        switch (argument.getResolveStrategy()) {
            case Cache:
                var mapper = new Mapper(argument.getIdentityStrategy());
                var bulkUploaderInternal = new BulkUploaderInternal();
                uploadFunction = exchangeDealsPackage -> bulkUploaderInternal.upload(connection, mapper.parse(exchangeDealsPackage));
                break;
            case Database:
                var bulkUploaderSource = new BulkUploaderSource();
                uploadFunction = exchangeDealsPackage -> bulkUploaderSource.upload(connection, exchangeDealsPackage.getObjects());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getResolveStrategy());
        }
    }

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws SQLException {
        statementBefore.execute();

        uploadFunction.accept(exchangeDealsPackage);

        statementAfter.setInt(1, exchangeDealsPackage.size());
        statementAfter.execute();
    }

    @SneakyThrows
    @Override
    public void close() {
        statementAfter.close();
    }

    @FunctionalInterface
    private interface UploadFunction {
        void accept(ExchangeDealsPackage exchangeDealsPackage) throws SQLException;
    }
}




