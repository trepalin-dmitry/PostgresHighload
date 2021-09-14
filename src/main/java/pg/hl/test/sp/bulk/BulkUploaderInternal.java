package pg.hl.test.sp.bulk;

import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import pg.hl.test.sp.bulk.ee.ExchangeDealPersonInternalExtended;
import pg.hl.test.sp.bulk.ee.ExchangeDealStatusInternalExtended;
import pg.hl.test.sp.bulk.mapping.ExchangeDealInternalMapping;
import pg.hl.test.sp.bulk.mapping.ExchangeDealPersonInternalExtendedMapping;
import pg.hl.test.sp.bulk.mapping.ExchangeDealStatusInternalExtendedMapping;
import pg.hl.test.ei.ExchangeDealInternal;
import pg.hl.test.ei.ExchangeDealPersonInternal;
import pg.hl.test.ei.ExchangeDealStatusInternal;

import java.util.Collection;

public class BulkUploaderInternal extends BulkUploader<ExchangeDealInternal, ExchangeDealPersonInternal, ExchangeDealPersonInternalExtended, ExchangeDealStatusInternal, ExchangeDealStatusInternalExtended> {
    @Override
    protected ExchangeDealPersonInternalExtended extendDealPerson(ExchangeDealPersonInternal exchangeDealPersonInternal, ExchangeDealInternal exchangeDealInternal) {
        return new ExchangeDealPersonInternalExtended(exchangeDealPersonInternal, exchangeDealInternal);
    }

    @Override
    protected ExchangeDealStatusInternalExtended extendDealStatus(ExchangeDealStatusInternal exchangeDealStatusInternal, ExchangeDealInternal exchangeDealInternal) {
        return new ExchangeDealStatusInternalExtended(exchangeDealStatusInternal, exchangeDealInternal);
    }

    @Override
    protected AbstractMapping<ExchangeDealInternal> createDealMapping() {
        return new ExchangeDealInternalMapping();
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
    protected Collection<ExchangeDealPersonInternal> getPersons(ExchangeDealInternal exchangeDealInternal) {
        return exchangeDealInternal.getExchangeDealPersons();
    }

    @Override
    protected Collection<ExchangeDealStatusInternal> getStatuses(ExchangeDealInternal exchangeDealInternalExtended) {
        return exchangeDealInternalExtended.getExchangeDealStatuses();
    }
}
