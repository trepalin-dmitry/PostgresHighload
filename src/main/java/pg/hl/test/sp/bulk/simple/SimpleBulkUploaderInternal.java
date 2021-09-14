package pg.hl.test.sp.bulk.simple;

import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import pg.hl.test.sp.bulk.simple.mapping.SimpleExchangeDealInternalMapping;
import pg.hl.test.sp.ei.simple.SimpleExchangeDealInternal;

public class SimpleBulkUploaderInternal extends SimpleBulkUploader<SimpleExchangeDealInternal> {

    @Override
    protected AbstractMapping<SimpleExchangeDealInternal> createDealMapping() {
        return new SimpleExchangeDealInternalMapping();
    }
}