package pg.hl.test.edc;

import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.common.ExchangeDealStatusType;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class StatusesTypesExistsDataItem extends ExistsDataItem<ExchangeDealStatusType, Character, String, AtomicInteger> {

    public StatusesTypesExistsDataItem(HibernateTestItem<?, ?, ?> defaultTestItem, int size) {
        super(defaultTestItem, size, ExchangeDealStatusType.class, String.class);
    }

    @Override
    protected String[] createCodesArray(int size) {
        return new String[size];
    }

    @Override
    protected Collection<ExchangeDealStatusType> getSource(HibernateTestItem<?, ?, ?> defaultTestItem) {
        return defaultTestItem.findStatusesTypes(Integer.MAX_VALUE);
    }

    @Override
    protected Character getId(ExchangeDealStatusType exchangeDealStatusType) {
        return exchangeDealStatusType.getId();
    }

    @Override
    protected String getCode(ExchangeDealStatusType exchangeDealStatusType) {
        return exchangeDealStatusType.getCode();
    }

    @Override
    protected AtomicInteger createInitArgument() {
        return new AtomicInteger(65);
    }

    @Override
    protected void modifyNew(ExchangeDealStatusType exchangeDealStatusType, AtomicInteger atomicInteger) {
        exchangeDealStatusType.setId((char) atomicInteger.getAndIncrement());
    }
}