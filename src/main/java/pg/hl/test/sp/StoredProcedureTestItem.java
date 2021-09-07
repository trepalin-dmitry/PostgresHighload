package pg.hl.test.sp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.CreateTestItemArgument;
import pg.hl.test.ProxyException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StoredProcedureTestItem extends AbstractTestItem {
    private final CreateTestItemArgument argument;
    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private static final StoredProcedureTestItemMapper mapper = new StoredProcedureTestItemMapper();

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private final CallableStatement callableStatement;

    public StoredProcedureTestItem(CreateTestItemArgument argument) throws SQLException {
        this.argument = argument;
        String url = "jdbc:postgresql://localhost/postgresHighLoad?user=postgres&password=postgres";
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);

        String sql = String.join("", "CALL public.\"exchangeDeals@Save." + argument.getResolveStrategy() + "." + argument.getIdentityStrategy() + "\"( ? );");
        callableStatement = connection.prepareCall(sql);
    }

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws ProxyException {
        try {
            String jsonArray;
            switch (argument.getResolveStrategy()){
                case Cache:
                    jsonArray = objectMapper.writeValueAsString(mapper.parse(exchangeDealsPackage));
                    break;
                case Database:
                    jsonArray = objectMapper.writeValueAsString(exchangeDealsPackage.getObjects());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + argument.getResolveStrategy());
            }

            System.out.println("jsonArray = " + jsonArray);

            callableStatement.setString(1, jsonArray);
            callableStatement.execute();
        } catch (JsonProcessingException | SQLException e) {
            throw new ProxyException(e);
        }
    }

    @SneakyThrows
    @Override
    public void close() {
        callableStatement.close();
    }
}

