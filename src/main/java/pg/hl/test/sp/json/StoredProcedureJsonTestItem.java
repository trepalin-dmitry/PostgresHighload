package pg.hl.test.sp.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.CreateTestItemArgument;
import pg.hl.test.sp.StoredProcedureTestItem;
import pg.hl.test.sp.ei.Mapper;

import java.beans.PropertyVetoException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Collection;

public class StoredProcedureJsonTestItem extends StoredProcedureTestItem<ExchangeDealsPackage, ExchangeDealSource> {
    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private final Mapper mapper;
    private final CallableStatement callableStatement;

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public StoredProcedureJsonTestItem(CreateTestItemArgument argument) throws PropertyVetoException, SQLException {
        super(argument, ExchangeDealsPackage.class);

        this.callableStatement = this.getConnection().prepareCall("CALL public.\"exchangeDeals@Save." + this.getArgument().getResolveStrategy() + "." + this.getArgument().getIdentityStrategy() + "\"(?, ?);");
        this.mapper = new Mapper(this.getArgument().getIdentityStrategy());
    }

    @Override
    protected void uploadDeals(ExchangeDealsPackage exchangeDealsPackage) throws JsonProcessingException, SQLException {
        Collection<?> collection;
        switch (this.getArgument().getResolveStrategy()) {
            case Cache:
                collection = mapper.parse(exchangeDealsPackage);
                break;
            case Database:
                collection = exchangeDealsPackage.getObjects();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.getArgument().getResolveStrategy());
        }

        callableStatement.setString(1, objectMapper.writeValueAsString(collection));
        callableStatement.setInt(2, collection.size());
        callableStatement.execute();
    }

    @Override
    public void close() throws SQLException {
        if (callableStatement != null){
            callableStatement.close();
        }

        super.close();
    }
}