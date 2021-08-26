package pg.hl.test.hb.simple;

import org.apache.commons.beanutils.BeanUtils;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.jpa.ExchangeDeal;
import pg.hl.jpa.ExchangeDealPerson;
import pg.hl.jpa.ExchangeDealStatus;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.RunArgument;
import pg.hl.test.TestType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HibernateSimpleTestItem extends AbstractTestItem {
    public HibernateSimpleTestItem(int threadsCount, int exchangeDealsSize, int exchangeDealsPersonsSize, int exchangeDealsStatusesSize) {
        super(TestType.HibernateSimple, threadsCount, exchangeDealsSize, exchangeDealsPersonsSize, exchangeDealsStatusesSize);
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

                var persons = copyList(exchangeDealSource.getPersons(), ExchangeDealPerson.class);
                exchangeDeal.addPersonsAll(persons);

                var statuses = copyList(exchangeDealSource.getStatuses(), ExchangeDealStatus.class);
                exchangeDeal.addStatusesAll(statuses);

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

    protected <TypeFrom, TypeTo> List<TypeTo> copyList(List<TypeFrom> list, Class<TypeTo> clazz) {
        return list
                .stream()
                .map(itemFrom -> {
                    TypeTo itemTo;
                    try {
                        itemTo = clazz.getDeclaredConstructor().newInstance();
                        BeanUtils.copyProperties(itemTo, itemFrom);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return itemTo;
                })
                .collect(Collectors.toList());
    }
}

