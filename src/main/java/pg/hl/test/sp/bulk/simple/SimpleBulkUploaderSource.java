package pg.hl.test.sp.bulk.simple;

import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import pg.hl.dto.simple.SimpleExchangeDealSource;
import pg.hl.test.sp.bulk.simple.mapping.SimpleExchangeDealSourceMapping;

public class SimpleBulkUploaderSource extends SimpleBulkUploader<SimpleExchangeDealSource> {

    @Override
    protected AbstractMapping<SimpleExchangeDealSource> createDealMapping() {
        return new SimpleExchangeDealSourceMapping();
    }
}