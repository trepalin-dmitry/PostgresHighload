package pg.hl.test;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.hb.*;
import pg.hl.test.hb.identity.HibernateTestItemIdentity;
import pg.hl.test.hb.sequence.HibernateTestItemSequence;
import pg.hl.test.sp.StoredProcedureTestItem;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TestUtils {
    public static final EasyRandom EASY_RANDOM;

    static {
        // Создаем генератор
        EasyRandomParameters easyRandomParameters = new EasyRandomParameters();
        easyRandomParameters.setSeed(new Random().nextLong());
        EASY_RANDOM = new EasyRandom(easyRandomParameters);
    }

    public static ExchangeDealsPackage createPackage(CreatePackageArgument argument) throws SQLException {
        var exchangeDealSourceList = EASY_RANDOM
                .objects(ExchangeDealSource.class, argument.getSize())
                .peek(exchangeDealSource -> exchangeDealSource.getPersons().addAll(
                        EASY_RANDOM.objects(ExchangeDealPersonSource.class, argument.getPersonsSize())
                                .peek(exchangeDealPersonSource -> exchangeDealPersonSource.setPersonGUId(ExistsDataController.getRandomPersonGuid()))
                                .collect(Collectors.toList())))
                .peek(exchangeDealSource -> {
                    AtomicInteger index = new AtomicInteger(1);
                    exchangeDealSource.getStatuses().addAll(
                            EASY_RANDOM.objects(ExchangeDealStatusSource.class, argument.getStatusesSize())
                                    .peek(exchangeDealStatusSource -> {
                                        exchangeDealStatusSource.setIndex(index.getAndIncrement());
                                        exchangeDealStatusSource.setTypeCode(ExistsDataController.getRandomStatusCode());
                                    })
                                    .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());

        // Устанавливаем имеющиеся GUId
        ExistsDataController.populateDeals(argument.getSizeExists());
        for (int i = 0; i < argument.getSizeExists(); i++) {
            exchangeDealSourceList.get(i).setGuid(ExistsDataController.getDealGuid(i));
        }

        return new ExchangeDealsPackage(exchangeDealSourceList);
    }

    public static HibernateTestItemIdentity createDefaultTestItem() throws SQLException {
        return (HibernateTestItemIdentity) createTestItem(new CreateTestItemArgument(TestItemsCodes.Hibernate.Hikari.Each.Before, ResolveStrategy.Cache, IdentityStrategy.Identity));
    }

    public static TestItem createTestItem(CreateTestItemArgument argument) throws SQLException {
        switch (argument.getCode()) {
            case TestItemsCodes.StoredProcedure:
                return new StoredProcedureTestItem(argument);

            case TestItemsCodes.Hibernate.C3p0.Each.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.C3p0, SaveStrategy.Each, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.C3p0.Each.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.C3p0, SaveStrategy.Each, CheckExistsStrategy.Before));
            case TestItemsCodes.Hibernate.C3p0.Batch.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.C3p0, SaveStrategy.Batch, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.C3p0.Batch.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.C3p0, SaveStrategy.Batch, CheckExistsStrategy.Before));

            case TestItemsCodes.Hibernate.Hikari.Each.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.Hikari, SaveStrategy.Each, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.Hikari.Each.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.Hikari, SaveStrategy.Each, CheckExistsStrategy.Before));
            case TestItemsCodes.Hibernate.Hikari.Batch.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.Hikari, SaveStrategy.Batch, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.Hikari.Batch.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.Hikari, SaveStrategy.Batch, CheckExistsStrategy.Before));

            default:
                throw new IllegalStateException("Unexpected value: " + argument.getCode());
        }
    }

    private static TestItem createHibernateTestItem(CreateHibernateTestItemArgument argument) {
        switch (argument.getParentArgument().getIdentityStrategy()) {
            case Identity:
                return new HibernateTestItemIdentity(argument);
            case Sequence:
                return new HibernateTestItemSequence(argument);
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getParentArgument().getIdentityStrategy());
        }
    }

    public static void main(String[] args) throws SQLException {
        try (var item = createDefaultTestItem()) {
            item.findDeals(1);
        }
    }
}

