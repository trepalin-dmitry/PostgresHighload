package pg.hl.test.hb.simple;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.jpa.ExchangeDeal;
import pg.hl.jpa.ExchangeDealPerson;
import pg.hl.jpa.ExchangeDealStatus;
import pg.hl.test.AbstractTestItem;
import pg.hl.test.RunArgument;
import pg.hl.test.TestArgument;
import ru.vtb.zf.common.data.naming.PhysicalNamingStrategyQuotedImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class HibernateCoreTestItem extends AbstractTestItem {
    SessionFactory sessionFactory = null;

    public HibernateCoreTestItem(String caption, TestArgument params) {
        super(caption, params);
    }

    @Override
    protected void runInternal(RunArgument argument) throws InvocationTargetException, IllegalAccessException {
        try (var userService = new ExchangeDealService(createSession())) {

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
        try (var userService = new ExchangeDealService(createSession())) {
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

    private Session createSession() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(ExchangeDeal.class);
            configuration.addAnnotatedClass(ExchangeDealPerson.class);
            configuration.addAnnotatedClass(ExchangeDealStatus.class);
            configuration.setPhysicalNamingStrategy(new PhysicalNamingStrategyQuotedImpl()); // Через конфигурационный файл не работает (хотя инициализируется)

            configuration = processConfiguration(configuration);

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        }
        return sessionFactory.openSession();
    }

    protected abstract Configuration processConfiguration(Configuration configuration);
}

