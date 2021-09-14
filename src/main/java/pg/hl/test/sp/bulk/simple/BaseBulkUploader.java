package pg.hl.test.sp.bulk.simple;

import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;
import java.util.Collection;

public abstract class BaseBulkUploader<TDealSource> {
    public abstract void upload(PgConnection pgConnection, Collection<TDealSource> collection) throws SQLException;
}
