package pg.hl.test.sp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.ProxyException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StoredProcedureTestItem extends AbstractTestItem {
    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private final CallableStatement callableStatement;

    public StoredProcedureTestItem() throws SQLException {
        String url = "jdbc:postgresql://localhost/postgresHighLoad?user=postgres&password=postgres";
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);
        callableStatement = connection.prepareCall("CALL public.\"exchangeDeals@Save\"( ? );");
    }

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws ProxyException {
        try {
            String jsonArray = objectMapper.writeValueAsString(exchangeDealsPackage.getObjects());
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
