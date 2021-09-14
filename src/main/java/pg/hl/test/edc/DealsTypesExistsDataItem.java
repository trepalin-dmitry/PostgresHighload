package pg.hl.test.edc;

import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.common.ExchangeDealType;

import java.util.Collection;

public class DealsTypesExistsDataItem extends ExistsDataItem<ExchangeDealType, Integer, String, Object> {

    public DealsTypesExistsDataItem(HibernateTestItem<?, ?, ?> defaultTestItem, int size) {
        super(defaultTestItem, size, ExchangeDealType.class, String.class);
    }

    @Override
    protected String[] createCodesArray(int size) {
        return new String[size];
    }

    @Override
    protected Collection<ExchangeDealType> getSource(HibernateTestItem<?, ?, ?> defaultTestItem) {
        return defaultTestItem.findDealsTypes(Integer.MAX_VALUE);
    }

    @Override
    protected Integer getId(ExchangeDealType exchangeDealType) {
        return exchangeDealType.getId();
    }

    @Override
    protected String getCode(ExchangeDealType exchangeDealType) {
        return exchangeDealType.getCode();
    }

    @Override
    protected Object createInitArgument() {
        return null;
    }

    @Override
    protected void modifyNew(ExchangeDealType exchangeDealType, Object o) {
    }
}
