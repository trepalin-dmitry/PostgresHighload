package pg.hl.test;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.hb.*;
import pg.hl.test.hb.identity.HibernateTestItemIdentity;
import pg.hl.test.hb.sequence.batch.HibernateTestItemSequenceBatch;
import pg.hl.test.hb.sequence.one.HibernateTestItemSequenceOne;
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
        ExistsDataController existsDataController = ExistsDataController.getOrCreate(argument.getIdentityStrategy());

        var exchangeDealSourceList = EASY_RANDOM
                .objects(ExchangeDealSource.class, argument.getSize())
                .peek(exchangeDealSource -> exchangeDealSource.getPersons().addAll(
                        EASY_RANDOM.objects(ExchangeDealPersonSource.class, argument.getPersonsSize())
                                .peek(exchangeDealPersonSource -> exchangeDealPersonSource.setPersonGUId(existsDataController.getRandomPersonGuid(exchangeDealSource.getGuid())))
                                .collect(Collectors.toList())))
                .peek(exchangeDealSource -> {
                    AtomicInteger index = new AtomicInteger(1);
                    exchangeDealSource.getStatuses().addAll(
                            EASY_RANDOM.objects(ExchangeDealStatusSource.class, argument.getStatusesSize())
                                    .peek(exchangeDealStatusSource -> {
                                        exchangeDealStatusSource.setIndex(index.getAndIncrement());
                                        exchangeDealStatusSource.setTypeCode(existsDataController.getRandomStatusCode(exchangeDealSource.getGuid()));
                                    })
                                    .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());

        // Устанавливаем имеющиеся GUId
        existsDataController.populateDeals(argument.getSizeExists());
        for (int i = 0; i < argument.getSizeExists(); i++) {
            exchangeDealSourceList.get(i).setGuid(existsDataController.getDealGuid(i));
        }

        return new ExchangeDealsPackage(exchangeDealSourceList);
    }

    public static HibernateTestItem<?> createDefaultTestItem(IdentityStrategy identityStrategy) throws SQLException {
        return (HibernateTestItem<?>) createTestItem(new CreateTestItemArgument(TestItemsCodes.Hibernate.Hikari.Min.Before, ResolveStrategy.Cache, identityStrategy));
    }

    public static TestItem createTestItem(CreateTestItemArgument argument) throws SQLException {
        switch (argument.getCode()) {
            case TestItemsCodes.StoredProcedure:
                return new StoredProcedureTestItem(argument);

            case TestItemsCodes.Hibernate.C3p0.Min.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.C3p0, BatchSize.Min, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.C3p0.Min.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.C3p0, BatchSize.Min, CheckExistsStrategy.Before));
            case TestItemsCodes.Hibernate.C3p0.Max.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.C3p0, BatchSize.Max, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.C3p0.Max.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.C3p0, BatchSize.Max, CheckExistsStrategy.Before));

            case TestItemsCodes.Hibernate.Hikari.Min.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.Hikari, BatchSize.Min, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.Hikari.Min.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.Hikari, BatchSize.Min, CheckExistsStrategy.Before));
            case TestItemsCodes.Hibernate.Hikari.Max.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.Hikari, BatchSize.Max, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.Hikari.Max.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, ConnectionPoolType.Hikari, BatchSize.Max, CheckExistsStrategy.Before));

            default:
                throw new IllegalStateException("Unexpected value: " + argument.getCode());
        }
    }

    private static TestItem createHibernateTestItem(CreateHibernateTestItemArgument argument) {
        switch (argument.getParentArgument().getIdentityStrategy()) {
            case Identity:
                return new HibernateTestItemIdentity(argument);
            case SequenceOne:
                return new HibernateTestItemSequenceOne(argument);
            case SequenceBatch:
                return new HibernateTestItemSequenceBatch(argument);
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getParentArgument().getIdentityStrategy());
        }
    }
}
