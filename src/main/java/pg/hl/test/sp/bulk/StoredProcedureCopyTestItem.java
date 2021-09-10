package pg.hl.test.sp.bulk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.CreateTestItemArgument;
import pg.hl.test.sp.ei.Mapper;

import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StoredProcedureCopyTestItem extends AbstractTestItem {
    private final CreateTestItemArgument argument;
    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private final Mapper mapper;
    private final BulkUploaderSource bulkUploaderSource;
    private final BulkUploaderInternal bulkUploaderInternal;


    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private final CallableStatement callableStatement;

    public StoredProcedureCopyTestItem(CreateTestItemArgument argument) throws SQLException {
        this.argument = argument;
        String url = "jdbc:postgresql://localhost/postgresHighLoad?user=postgres&password=postgres";
        var connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);
        String sql = String.join("", "CALL public.\"exchangeDeals@SaveCopy." + argument.getResolveStrategy() + "." + argument.getIdentityStrategy() + "\"(?, ?);");
        this.callableStatement = connection.prepareCall(sql);
        this.mapper = new Mapper(argument.getIdentityStrategy());

        this.bulkUploaderSource = new BulkUploaderSource(connection);
        this.bulkUploaderInternal = new BulkUploaderInternal(connection);
    }

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws SQLException {
        BulkUploader.UploadResult uploadResult;

        switch (argument.getResolveStrategy()) {
            case Database:
                uploadResult = this.bulkUploaderSource.upload(exchangeDealsPackage.getObjects());
                break;
            case Cache:
                uploadResult = this.bulkUploaderInternal.upload(mapper.parse(exchangeDealsPackage));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getResolveStrategy());
        }

        callableStatement.setObject(1, uploadResult.getUploadKey());
        callableStatement.setInt(2, exchangeDealsPackage.size());
        callableStatement.execute();
    }

    @SneakyThrows
    @Override
    public void close() {
        callableStatement.close();
    }
}




