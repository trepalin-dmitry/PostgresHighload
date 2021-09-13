package pg.hl.test.sp.bulk;

import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.test.sp.bulk.ee.ExchangeDealPersonSourceExtended;
import pg.hl.test.sp.bulk.ee.ExchangeDealSourceExtended;
import pg.hl.test.sp.bulk.ee.ExchangeDealStatusSourceExtended;
import pg.hl.test.sp.bulk.mapping.ExchangeDealPersonSourceExtendedMapping;
import pg.hl.test.sp.bulk.mapping.ExchangeDealSourceExtendedMapping;
import pg.hl.test.sp.bulk.mapping.ExchangeDealStatusSourceExtendedMapping;

import java.sql.Connection;
import java.util.Collection;
import java.util.UUID;

public class BulkUploaderSource extends BulkUploader<ExchangeDealSource, ExchangeDealSourceExtended, ExchangeDealPersonSource, ExchangeDealPersonSourceExtended, ExchangeDealStatusSource, ExchangeDealStatusSourceExtended> {
    public BulkUploaderSource(Connection connection) {
        super(connection);
    }

    @Override
    protected ExchangeDealSourceExtended extendDeal(ExchangeDealSource exchangeDealSource, UUID uuid) {
        return new ExchangeDealSourceExtended(exchangeDealSource, uuid);
    }

    @Override
    protected ExchangeDealPersonSourceExtended extendDealPerson(ExchangeDealPersonSource exchangeDealPersonSource, ExchangeDealSourceExtended exchangeDealSourceExtended) {
        return new ExchangeDealPersonSourceExtended(exchangeDealPersonSource, exchangeDealSourceExtended);
    }

    @Override
    protected ExchangeDealStatusSourceExtended extendDealStatus(ExchangeDealStatusSource source, ExchangeDealSourceExtended exchangeDealSourceExtended) {
        return new ExchangeDealStatusSourceExtended(source, exchangeDealSourceExtended);
    }

    @Override
    protected AbstractMapping<ExchangeDealSourceExtended> createDealMapping() {
        return new ExchangeDealSourceExtendedMapping();
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
    protected Collection<ExchangeDealPersonSource> getPersons(ExchangeDealSourceExtended exchangeDealSourceExtended) {
        return exchangeDealSourceExtended.getDeal().getPersons();
    }

    @Override
    protected Collection<ExchangeDealStatusSource> getStatuses(ExchangeDealSourceExtended exchangeDealSourceExtended) {
        return exchangeDealSourceExtended.getDeal().getStatuses();
    }
}

