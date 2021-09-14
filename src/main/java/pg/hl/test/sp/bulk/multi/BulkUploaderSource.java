package pg.hl.test.sp.bulk.multi;

import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import pg.hl.dto.multi.ExchangeDealPersonSource;
import pg.hl.dto.multi.ExchangeDealSource;
import pg.hl.dto.multi.ExchangeDealStatusSource;
import pg.hl.test.sp.bulk.multi.ee.ExchangeDealPersonSourceExtended;
import pg.hl.test.sp.bulk.multi.ee.ExchangeDealStatusSourceExtended;
import pg.hl.test.sp.bulk.multi.mapping.ExchangeDealPersonSourceExtendedMapping;
import pg.hl.test.sp.bulk.multi.mapping.ExchangeDealSourceMapping;
import pg.hl.test.sp.bulk.multi.mapping.ExchangeDealStatusSourceExtendedMapping;

import java.util.Collection;

public class BulkUploaderSource extends BulkUploader<ExchangeDealSource, ExchangeDealPersonSource, ExchangeDealPersonSourceExtended, ExchangeDealStatusSource, ExchangeDealStatusSourceExtended> {
    @Override
    protected ExchangeDealPersonSourceExtended extendDealPerson(ExchangeDealPersonSource exchangeDealPersonSource, ExchangeDealSource exchangeDealSource) {
        return new ExchangeDealPersonSourceExtended(exchangeDealPersonSource, exchangeDealSource);
    }

    @Override
    protected ExchangeDealStatusSourceExtended extendDealStatus(ExchangeDealStatusSource source, ExchangeDealSource exchangeDealSource) {
        return new ExchangeDealStatusSourceExtended(source, exchangeDealSource);
    }

    @Override
    protected AbstractMapping<ExchangeDealSource> createDealMapping() {
        return new ExchangeDealSourceMapping();
    }

    @Override
    protected AbstractMapping<ExchangeDealPersonSourceExtended> createDealPersonMapping() {
        return new ExchangeDealPersonSourceExtendedMapping();
    }

    @Override
    protected AbstractMapping<ExchangeDealStatusSourceExtended> createDealStatusMapping() {
        return new ExchangeDealStatusSourceExtendedMapping();
    }

    @Override
    protected Collection<ExchangeDealPersonSource> getPersons(ExchangeDealSource exchangeDealSourceExtended) {
        return exchangeDealSourceExtended.getPersons();
    }

    @Override
    protected Collection<ExchangeDealStatusSource> getStatuses(ExchangeDealSource exchangeDealSourceExtended) {
        return exchangeDealSourceExtended.getStatuses();
    }
}

