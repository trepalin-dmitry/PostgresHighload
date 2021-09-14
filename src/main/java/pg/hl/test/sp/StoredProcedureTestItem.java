package pg.hl.test.sp;

import lombok.AccessLevel;
import lombok.Getter;
import pg.hl.dto.AbstractDataObject;
import pg.hl.dto.AbstractDataPackage;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.CreateTestItemArgument;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class StoredProcedureTestItem<
        TypePackage extends AbstractDataPackage<TypeExchangeDealSource>,
        TypeExchangeDealSource extends AbstractDataObject
        > extends AbstractTestItem<TypePackage, TypeExchangeDealSource> {

    @Getter(AccessLevel.PROTECTED)
    private final Connection connection;

    protected StoredProcedureTestItem(CreateTestItemArgument argument, Class<TypePackage> typePackageClazz) throws PropertyVetoException, SQLException {
        super(argument, typePackageClazz);

        this.connection = ConnectionPoolController.getConnection(argument.getConnectionPoolType());
    }

    @Override
    public void close() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        }

        super.close();
    }
}
