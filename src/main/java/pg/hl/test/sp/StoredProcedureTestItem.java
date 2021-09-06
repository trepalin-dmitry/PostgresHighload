package pg.hl.test.sp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.ProxyException;
import pg.hl.test.ResolveStrategy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StoredProcedureTestItem extends AbstractTestItem {
    private final ResolveStrategy resolveStrategy;
    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private static final StoredProcedureTestItemMapper mapper = new StoredProcedureTestItemMapper();

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private final CallableStatement callableStatement;

    public StoredProcedureTestItem(ResolveStrategy resolveStrategy) throws SQLException {
        this.resolveStrategy = resolveStrategy;
        String url = "jdbc:postgresql://localhost/postgresHighLoad?user=postgres&password=postgres";
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);
        switch (resolveStrategy){
            case Cache:
                callableStatement = connection.prepareCall("CALL public.\"exchangeDeals@Save.Ids\"( ? );");
                break;
            case Database:
                callableStatement = connection.prepareCall("CALL public.\"exchangeDeals@Save.Codes\"( ? );");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + resolveStrategy);
        }
    }

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws ProxyException {
        try {
            String jsonArray;
            switch (resolveStrategy){
                case Cache:
                    jsonArray = objectMapper.writeValueAsString(mapper.parse(exchangeDealsPackage));
                    break;
                case Database:
                    jsonArray = objectMapper.writeValueAsString(exchangeDealsPackage.getObjects());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + resolveStrategy);
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

