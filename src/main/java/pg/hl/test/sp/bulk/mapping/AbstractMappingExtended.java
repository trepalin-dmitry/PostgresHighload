package pg.hl.test.sp.bulk.mapping;

import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import de.bytefish.pgbulkinsert.pgsql.handlers.IValueHandlerProvider;

public class AbstractMappingExtended<T> extends AbstractMapping<T> {

    protected AbstractMappingExtended(String schemaName, String tableName) {
        super(schemaName, tableName, true);
    }

    protected AbstractMappingExtended(String schemaName, String tableName, boolean usePostgresQuoting) {
        super(schemaName, tableName, usePostgresQuoting);
    }

    protected AbstractMappingExtended(IValueHandlerProvider provider, String schemaName, String tableName, boolean usePostgresQuoting) {
        super(provider, schemaName, tableName, usePostgresQuoting);
    }
}
