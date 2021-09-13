package pg.hl.test.sp.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.CreateTestItemArgument;
import pg.hl.test.sp.ei.Mapper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

public class StoredProcedureJsonTestItem extends AbstractTestItem {
    private final CreateTestItemArgument argument;
    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private final Mapper mapper;

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private final CallableStatement callableStatement;

    public StoredProcedureJsonTestItem(CreateTestItemArgument argument) throws SQLException {
        this.argument = argument;
        String url = "jdbc:postgresql://localhost/postgresHighLoad?user=postgres&password=postgres";
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);
        String sql = String.join("", "CALL public.\"exchangeDeals@Save." + argument.getResolveStrategy() + "." + argument.getIdentityStrategy() + "\"(?, ?);");
        callableStatement = connection.prepareCall(sql);
        this.mapper = new Mapper(argument.getIdentityStrategy());
    }

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws JsonProcessingException, SQLException {
        Collection<?> collection;
        switch (argument.getResolveStrategy()) {
            case Cache:
                collection = mapper.parse(exchangeDealsPackage);
                break;
            case Database:
                collection = exchangeDealsPackage.getObjects();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getResolveStrategy());
        }

        callableStatement.setString(1, objectMapper.writeValueAsString(collection));
        callableStatement.setInt(2, collection.size());
        callableStatement.execute();
    }

    @SneakyThrows
    @Override
    public void close() {
        callableStatement.close();
    }
}


