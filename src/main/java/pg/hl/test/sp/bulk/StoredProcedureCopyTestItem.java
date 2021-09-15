package pg.hl.test.sp.bulk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.postgresql.jdbc.PgConnection;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pg.hl.dto.AbstractDataObject;
import pg.hl.dto.AbstractDataPackage;
import pg.hl.test.CreateTestItemArgument;
import pg.hl.test.sp.StoredProcedureTestItem;
import pg.hl.test.sp.bulk.simple.BaseBulkUploader;
import pg.hl.test.sp.ei.AbstractMapper;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class StoredProcedureCopyTestItem<
        TypePackage extends AbstractDataPackage<TypeExchangeDealSource>,
        TypeExchangeDealSource extends AbstractDataObject,
        TypeExchangeDealTarget,
        TypeMapper extends AbstractMapper<TypePackage, TypeExchangeDealSource, TypeExchangeDealTarget>,
        TypeBulkUploaderDatabase extends BaseBulkUploader<TypeExchangeDealSource>,
        TypeBulkUploaderCache extends BaseBulkUploader<TypeExchangeDealTarget>
        > extends StoredProcedureTestItem<TypePackage, TypeExchangeDealSource, TypeExchangeDealTarget, TypeMapper> {
    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private final UploadFunction<TypePackage> uploadFunction;
    private final CallableStatement statementBefore;
    private final CallableStatement statementAfter;
    private final PgConnection pgConnection;

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public StoredProcedureCopyTestItem(CreateTestItemArgument argument, Class<TypePackage> typePackageClazz, Class<TypeMapper> typeMapperClazz, Class<TypeBulkUploaderDatabase> typeBulkUploaderDatabaseClazz, Class<TypeBulkUploaderCache> typeBulkUploaderCacheClazz) throws PropertyVetoException, SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        super(argument, typePackageClazz, typeMapperClazz);

        this.pgConnection = this.getConnection().unwrap(PgConnection.class);

        try (var statementInit = this.getConnection().prepareStatement("call \"" + getSqlEntity() + "@CreateTables." + argument.getResolveStrategy() + "\"();")) {
            statementInit.execute();
        }

        this.statementBefore = this.getConnection().prepareCall("call \"" + getSqlEntity() + "@CleanupTables." + argument.getResolveStrategy() + "\"();");
        this.statementAfter = this.getConnection().prepareCall("call \"" + getSqlEntity() + "@SaveCopy." + argument.getResolveStrategy() + "." + argument.getIdentityStrategy() + "\"(?);");

        switch (argument.getResolveStrategy()) {
            case Cache:
                var bulkUploaderInternal = createBulkUploader(typeBulkUploaderCacheClazz);
                this.uploadFunction = exchangeDealsPackage -> bulkUploaderInternal.upload(this.pgConnection, getMapper().parse(exchangeDealsPackage));
                break;
            case Database:
                var bulkUploaderSource = createBulkUploader(typeBulkUploaderDatabaseClazz);
                this.uploadFunction = exchangeDealsPackage -> bulkUploaderSource.upload(this.pgConnection, exchangeDealsPackage.getObjects());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getResolveStrategy());
        }
    }

    protected <G, T extends BaseBulkUploader<G>> T createBulkUploader(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz
                .getConstructor()
                .newInstance();
    }

    @Override
    protected void uploadDeals(TypePackage exchangeDealsPackage) throws SQLException {
        statementBefore.execute();

        uploadFunction.accept(exchangeDealsPackage);

        statementAfter.setInt(1, exchangeDealsPackage.size());
        statementAfter.execute();
    }

    @FunctionalInterface
    private interface UploadFunction<TypePackage> {
        void accept(TypePackage exchangeDealsPackage) throws SQLException;
    }

    @Override
    public void close() throws SQLException {
        if (this.statementBefore != null) {
            this.statementBefore.close();
        }

        if (this.statementAfter != null) {
            this.statementAfter.close();
        }

        super.close();
    }
}




