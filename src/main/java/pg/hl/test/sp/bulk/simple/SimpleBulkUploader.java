package pg.hl.test.sp.bulk.simple;

import de.bytefish.pgbulkinsert.PgBulkInsert;
import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;
import java.util.Collection;

public abstract class SimpleBulkUploader<TDealSource> extends BaseBulkUploader<TDealSource> {
    private final PgBulkInsert<TDealSource> pgBulkInsertExchangeDealSourceExtended;

    public SimpleBulkUploader() {
        this.pgBulkInsertExchangeDealSourceExtended = new PgBulkInsert<>(createDealMapping());
    }

    public void upload(PgConnection pgConnection, Collection<TDealSource> collection) throws SQLException {
        pgBulkInsertExchangeDealSourceExtended.saveAll(pgConnection, collection);
    }

    protected abstract AbstractMapping<TDealSource> createDealMapping();
}

