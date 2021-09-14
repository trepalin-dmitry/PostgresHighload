package pg.hl.test.sp;

import lombok.AccessLevel;
import lombok.Getter;
import pg.hl.dto.AbstractDataObject;
import pg.hl.dto.AbstractDataPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.CreateTestItemArgument;
import pg.hl.test.EntityType;
import pg.hl.test.IdentityStrategy;
import pg.hl.test.sp.ei.AbstractMapper;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class StoredProcedureTestItem<
        TypePackage extends AbstractDataPackage<TypeExchangeDealSource>,
        TypeExchangeDealSource extends AbstractDataObject,
        TypeExchangeDealTarget,
        TypeMapper extends AbstractMapper<TypePackage, TypeExchangeDealSource, TypeExchangeDealTarget>
        > extends AbstractTestItem<TypePackage, TypeExchangeDealSource> {

    @Getter(AccessLevel.PROTECTED)
    private final Connection connection;
    @Getter(AccessLevel.PROTECTED)
    private final String sqlEntity;
    @Getter(AccessLevel.PROTECTED)
    private final TypeMapper mapper;

    protected StoredProcedureTestItem(CreateTestItemArgument argument, Class<TypePackage> typePackageClazz, Class<TypeMapper> typeMapperClazz) throws PropertyVetoException, SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(argument, typePackageClazz);

        this.connection = ConnectionPoolController.getConnection(argument.getConnectionPoolType());

        switch (this.getArgument().getEntityType()){
            case Simple:
                this.sqlEntity = "simpleExchangeDeals";
                break;
            case Multi:
                this.sqlEntity = "exchangeDeals";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.getArgument().getEntityType());
        }

        this.mapper = typeMapperClazz
                .getConstructor(IdentityStrategy.class, EntityType.class)
                .newInstance(this.getArgument().getIdentityStrategy(), this.getArgument().getEntityType());
    }

    @Override
    public void close() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        }

        super.close();
    }
}
