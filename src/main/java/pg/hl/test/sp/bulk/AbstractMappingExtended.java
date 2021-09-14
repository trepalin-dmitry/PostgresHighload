package pg.hl.test.sp.bulk;

import de.bytefish.pgbulkinsert.mapping.AbstractMapping;

public class AbstractMappingExtended<T> extends AbstractMapping<T> {

    protected AbstractMappingExtended(String tableName) {
        super("", tableName, true);
    }
}
