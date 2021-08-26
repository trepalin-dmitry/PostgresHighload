package pg.hl.test.hb.simple;

import org.apache.commons.beanutils.BeanUtils;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.jpa.ExchangeDeal;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.RunArgument;
import pg.hl.test.TestType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class HibernateSimpleTestItem extends AbstractTestItem {
    public HibernateSimpleTestItem(int threadsCount, int packageSize) {
        super(TestType.HibernateSimple, threadsCount, packageSize);
    }

    @Override
    protected String getLoggerName() {
        return HibernateSimpleTestItem.class.getName();
    }

    @Override
    protected void runInternal(RunArgument argument) throws InvocationTargetException, IllegalAccessException {
        try (ExchangeDealService userService = new ExchangeDealService()) {

            var deals = new ArrayList<ExchangeDeal>();

            for (ExchangeDealSource exchangeDealSource : argument.getDealsPackage().getObjects()) {
                ExchangeDeal exchangeDeal = new ExchangeDeal();
                BeanUtils.copyProperties(exchangeDeal, exchangeDealSource);
                deals.add(exchangeDeal);
            }

            for (ExchangeDeal deal : deals) {
                userService.saveOrUpdateExchangeDeal(deal);
            }
        }
    }

    @Override
    public void cleanDatabase() {
        try (ExchangeDealService userService = new ExchangeDealService()) {
            var deals = userService.findAllExchangeDeals();
            for (ExchangeDeal deal : deals) {
                userService.deleteExchangeDeal(deal);
            }
        }
    }
}

