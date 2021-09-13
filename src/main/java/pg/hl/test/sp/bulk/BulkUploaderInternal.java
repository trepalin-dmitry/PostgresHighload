package pg.hl.test.sp.bulk;

import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import pg.hl.test.sp.bulk.ee.ExchangeDealInternalExtended;
import pg.hl.test.sp.bulk.ee.ExchangeDealPersonInternalExtended;
import pg.hl.test.sp.bulk.ee.ExchangeDealStatusInternalExtended;
import pg.hl.test.sp.bulk.mapping.ExchangeDealInternalExtendedMapping;
import pg.hl.test.sp.bulk.mapping.ExchangeDealPersonInternalExtendedMapping;
import pg.hl.test.sp.bulk.mapping.ExchangeDealStatusInternalExtendedMapping;
import pg.hl.test.sp.ei.ExchangeDealInternal;
import pg.hl.test.sp.ei.ExchangeDealPersonInternal;
import pg.hl.test.sp.ei.ExchangeDealStatusInternal;

import java.sql.Connection;
import java.util.Collection;
import java.util.UUID;

public class BulkUploaderInternal extends BulkUploader<ExchangeDealInternal, ExchangeDealInternalExtended, ExchangeDealPersonInternal, ExchangeDealPersonInternalExtended, ExchangeDealStatusInternal, ExchangeDealStatusInternalExtended> {
    public BulkUploaderInternal(Connection connection) {
        super(connection);
    }

    @Override
    protected ExchangeDealInternalExtended extendDeal(ExchangeDealInternal exchangeDealInternal, UUID uuid) {
        return new ExchangeDealInternalExtended(exchangeDealInternal, uuid);
    }

    @Override
    protected ExchangeDealPersonInternalExtended extendDealPerson(ExchangeDealPersonInternal exchangeDealPersonInternal, ExchangeDealInternalExtended exchangeDealInternalExtended) {
        return new ExchangeDealPersonInternalExtended(exchangeDealPersonInternal, exchangeDealInternalExtended);
    }

    @Override
    protected ExchangeDealStatusInternalExtended extendDealStatus(ExchangeDealStatusInternal exchangeDealStatusInternal, ExchangeDealInternalExtended exchangeDealInternalExtended) {
        return new ExchangeDealStatusInternalExtended(exchangeDealStatusInternal, exchangeDealInternalExtended);
    }

    @Override
    protected AbstractMapping<ExchangeDealInternalExtended> createDealMapping() {
        return new ExchangeDealInternalExtendedMapping();
    }

    @Override
    protected AbstractMapping<ExchangeDealPersonInternalExtended> createDealPersonMapping() {
        return new ExchangeDealPersonInternalExtendedMapping();
    }

    @Override
    protected AbstractMapping<ExchangeDealStatusInternalExtended> createDealStatusMapping() {
        return new ExchangeDealStatusInternalExtendedMapping();
    }

    @Override
    protected Collection<ExchangeDealPersonInternal> getPersons(ExchangeDealInternalExtended exchangeDealInternalExtended) {
        return exchangeDealInternalExtended.getDeal().getExchangeDealPersons();
    }

    @Override
    protected Collection<ExchangeDealStatusInternal> getStatuses(ExchangeDealInternalExtended exchangeDealInternalExtended) {
        return exchangeDealInternalExtended.getDeal().getExchangeDealStatuses();
    }
}
