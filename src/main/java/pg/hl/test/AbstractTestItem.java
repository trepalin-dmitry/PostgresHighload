package pg.hl.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import pg.hl.dto.AbstractDataObject;
import pg.hl.dto.AbstractDataPackage;

import java.sql.SQLException;

public abstract class AbstractTestItem<
        TypePackage extends AbstractDataPackage<TypeExchangeDealSource>,
        TypeExchangeDealSource extends AbstractDataObject
        > extends BaseTestItem {

    @Getter(AccessLevel.PROTECTED)
    private final CreateTestItemArgument argument;
    private final Class<TypePackage> typePackageClazz;

    protected AbstractTestItem(CreateTestItemArgument argument, Class<TypePackage> typePackageClazz) {
        this.argument = argument;
        this.typePackageClazz = typePackageClazz;
    }

    @Override
    protected void uploadDealsCore(Object dataPackage) throws JsonProcessingException, SQLException {
        uploadDeals(typePackageClazz.cast(dataPackage));
    }

    protected abstract void uploadDeals(TypePackage abstractDataPackage) throws JsonProcessingException, SQLException;
}

