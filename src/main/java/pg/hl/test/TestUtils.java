package pg.hl.test;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.hb.ConnectionPoolType;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.SaveStrategy;
import pg.hl.test.sp.StoredProcedureTestItem;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TestUtils {
    private static final EasyRandom easyRandom;

    static {
        // Создаем генератор
        EasyRandomParameters easyRandomParameters = new EasyRandomParameters();
        easyRandomParameters.setSeed(new Random().nextLong());
        easyRandom = new EasyRandom(easyRandomParameters);
    }

    public static ExchangeDealsPackage createPackage(CreatePackageArgument argument) throws SQLException {
        var exchangeDealSourceList = easyRandom
                .objects(ExchangeDealSource.class, argument.getSize())
                .peek(exchangeDealSource -> exchangeDealSource.getPersons().addAll(
                        easyRandom.objects(ExchangeDealPersonSource.class, argument.getPersonsSize())
                                .peek(exchangeDealPersonSource -> exchangeDealPersonSource.setPersonGUId(ExistsDataController.getRandomPersonGuid()))
                                .collect(Collectors.toList())))
                .peek(exchangeDealSource -> {
                    AtomicInteger index = new AtomicInteger(1);
                    exchangeDealSource.getStatuses().addAll(
                            easyRandom.objects(ExchangeDealStatusSource.class, argument.getStatusesSize())
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

    public static HibernateTestItem createDefaultTestItem() throws SQLException {
        return (HibernateTestItem) createTestItem(new CreateTestItemArgument(TestItemsCodes.Hibernate.Hikari.Each, ResolveStrategy.Cache));
    }

    public static TestItem createTestItem(CreateTestItemArgument argument) throws SQLException {
        ResolveStrategy resolveStrategy = argument.getResolveStrategy();
        switch (argument.getCode()) {
            case TestItemsCodes.StoredProcedure:
                return new StoredProcedureTestItem(resolveStrategy);
            case TestItemsCodes.Hibernate.C3p0.Each:
                return new HibernateTestItem(resolveStrategy, ConnectionPoolType.C3p0, SaveStrategy.Each);
            case TestItemsCodes.Hibernate.C3p0.Batch.Check:
                return new HibernateTestItem(resolveStrategy, ConnectionPoolType.C3p0, SaveStrategy.BatchCheckExistsBefore);
            case TestItemsCodes.Hibernate.C3p0.Batch.NoCheck:
                return new HibernateTestItem(resolveStrategy, ConnectionPoolType.C3p0, SaveStrategy.BatchHandleException);
            case TestItemsCodes.Hibernate.Hikari.Each:
                return new HibernateTestItem(resolveStrategy, ConnectionPoolType.Hikari, SaveStrategy.Each);
            case TestItemsCodes.Hibernate.Hikari.Batch.Check:
                return new HibernateTestItem(resolveStrategy, ConnectionPoolType.Hikari, SaveStrategy.BatchCheckExistsBefore);
            case TestItemsCodes.Hibernate.Hikari.Batch.NoCheck:
                return new HibernateTestItem(resolveStrategy, ConnectionPoolType.Hikari, SaveStrategy.BatchHandleException);
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getCode());
        }
    }
}

