package pg.hl.test.sp.bulk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.postgresql.jdbc.PgConnection;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.CreateTestItemArgument;
import pg.hl.test.sp.StoredProcedureTestItem;
import pg.hl.test.sp.ei.Mapper;

import java.beans.PropertyVetoException;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class StoredProcedureCopyTestItem extends StoredProcedureTestItem<ExchangeDealsPackage, ExchangeDealSource> {
    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private final UploadFunction uploadFunction;
    private final CallableStatement statementBefore;
    private final CallableStatement statementAfter;
    private final PgConnection pgConnection;

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public StoredProcedureCopyTestItem(CreateTestItemArgument argument) throws PropertyVetoException, SQLException {
        super(argument, ExchangeDealsPackage.class);
        this.pgConnection = this.getConnection().unwrap(PgConnection.class);

        try (var statementInit = this.getConnection().prepareStatement("call \"exchangeDeals@CreateTables." + argument.getResolveStrategy() + "\"();")) {
            statementInit.execute();
        }

        this.statementBefore = this.getConnection().prepareCall("call \"exchangeDeals@CleanupTables." + argument.getResolveStrategy() + "\"();");
        this.statementAfter = this.getConnection().prepareCall("call \"exchangeDeals@SaveCopy." + argument.getResolveStrategy() + "." + argument.getIdentityStrategy() + "\"(?);");

        switch (argument.getResolveStrategy()) {
            case Cache:
                var mapper = new Mapper(argument.getIdentityStrategy());
                var bulkUploaderInternal = new BulkUploaderInternal();
                uploadFunction = exchangeDealsPackage -> bulkUploaderInternal.upload(this.pgConnection, mapper.parse(exchangeDealsPackage));
                break;
            case Database:
                var bulkUploaderSource = new BulkUploaderSource();
                uploadFunction = exchangeDealsPackage -> bulkUploaderSource.upload(this.pgConnection, exchangeDealsPackage.getObjects());
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

    @FunctionalInterface
    private interface UploadFunction {
        void accept(ExchangeDealsPackage exchangeDealsPackage) throws SQLException;
    }

    @Override
    public void close() throws SQLException {
        if (this.statementBefore != null) {
            this.statementBefore.close();
        }

        if (this.statementAfter != null) {
            this.statementAfter.close();
        }

        super.close();
    }
}




