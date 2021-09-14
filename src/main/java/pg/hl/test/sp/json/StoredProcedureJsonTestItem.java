package pg.hl.test.sp.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.AbstractDataObject;
import pg.hl.dto.AbstractDataPackage;
import pg.hl.test.CreateTestItemArgument;
import pg.hl.test.sp.StoredProcedureTestItem;
import pg.hl.test.sp.ei.AbstractMapper;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Collection;

public class StoredProcedureJsonTestItem<
        TypePackage extends AbstractDataPackage<TypeExchangeDealSource>,
        TypeExchangeDealSource extends AbstractDataObject,
        TypeExchangeDealTarget,
        TypeMapper extends AbstractMapper<TypePackage, TypeExchangeDealSource, TypeExchangeDealTarget>
        > extends StoredProcedureTestItem<TypePackage, TypeExchangeDealSource, TypeExchangeDealTarget, TypeMapper> {

    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private final CallableStatement callableStatement;

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public StoredProcedureJsonTestItem(CreateTestItemArgument argument, Class<TypePackage> typePackageClazz, Class<TypeMapper> typeMapperClazz) throws PropertyVetoException, SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(argument, typePackageClazz, typeMapperClazz);

        this.callableStatement = this.getConnection().prepareCall("CALL public.\"" + getSqlEntity() + "@Save." + this.getArgument().getResolveStrategy() + "." + this.getArgument().getIdentityStrategy() + "\"(?, ?);");
    }

    @Override
    protected void uploadDeals(TypePackage exchangeDealsPackage) throws JsonProcessingException, SQLException {
        Collection<?> collection;
        switch (this.getArgument().getResolveStrategy()) {
            case Cache:
                collection = getMapper().parse(exchangeDealsPackage);
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