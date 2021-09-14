package pg.hl.test;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import pg.hl.DevException;
import pg.hl.dto.*;
import pg.hl.test.hb.*;
import pg.hl.test.hb.identity.HibernateTestItemIdentity;
import pg.hl.test.hb.sequence.batch.HibernateTestItemSequenceBatch;
import pg.hl.test.hb.sequence.one.HibernateTestItemSequenceOne;
import pg.hl.test.sp.bulk.StoredProcedureCopyTestItem;
import pg.hl.test.sp.json.StoredProcedureJsonTestItem;

import java.beans.PropertyVetoException;
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

    public static Object createPackage(CreatePackageArgument argument) throws SQLException, PropertyVetoException {
        ExistsDataController existsDataController = ExistsDataController.getOrCreate(argument.getIdentityStrategy());

        switch (argument.getEntityType()) {
            case Simple:
                throw new DevException("Simple");
            case Multi:
                var exchangeDealSourceList = EASY_RANDOM
                        .objects(ExchangeDealSource.class, argument.getPackageSize())
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
                existsDataController.populateDeals(argument.getPackageSizeExists());
                for (int i = 0; i < argument.getPackageSizeExists(); i++) {
                    exchangeDealSourceList.get(i).setGuid(existsDataController.getDealGuid(i));
                }

                return new ExchangeDealsPackage(exchangeDealSourceList);
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getEntityType());
        }
    }

    public static HibernateTestItem<?, ?, ?> createDefaultTestItem(IdentityStrategy identityStrategy) throws SQLException, PropertyVetoException {
        return (HibernateTestItem<?, ?, ?>) createTestItem(new CreateTestItemArgument(TestItemsCodes.Hibernate.Min.Before, ResolveStrategy.Cache, identityStrategy, ConnectionPoolType.Hikari));
    }

    public static TestItem createTestItem(CreateTestItemArgument argument) throws SQLException, PropertyVetoException {
        switch (argument.getCode()) {
            case TestItemsCodes.StoredProcedure.Json:
                return new StoredProcedureJsonTestItem(argument);
            case TestItemsCodes.StoredProcedure.Bulk:
                return new StoredProcedureCopyTestItem(argument);

            case TestItemsCodes.Hibernate.Min.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, BatchSize.Min, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.Min.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, BatchSize.Min, CheckExistsStrategy.Before));
            case TestItemsCodes.Hibernate.Max.OnException:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, BatchSize.Max, CheckExistsStrategy.OnException));
            case TestItemsCodes.Hibernate.Max.Before:
                return createHibernateTestItem(new CreateHibernateTestItemArgument(argument, BatchSize.Max, CheckExistsStrategy.Before));

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
