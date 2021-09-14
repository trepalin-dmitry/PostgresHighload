package pg.hl.test;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import pg.hl.dto.multi.ExchangeDealPersonSource;
import pg.hl.dto.multi.ExchangeDealSource;
import pg.hl.dto.multi.ExchangeDealStatusSource;
import pg.hl.dto.multi.ExchangeDealsPackage;
import pg.hl.dto.simple.SimpleExchangeDealSource;
import pg.hl.dto.simple.SimpleExchangeDealsPackage;
import pg.hl.test.edc.ExistsDataController;
import pg.hl.test.hb.BatchSize;
import pg.hl.test.hb.CheckExistsStrategy;
import pg.hl.test.hb.CreateHibernateTestItemArgument;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.hb.entity.multi.identity.ExchangeDealIdentity;
import pg.hl.test.hb.entity.multi.identity.ExchangeDealSourceIdentityMapper;
import pg.hl.test.hb.entity.multi.sequence.batch.ExchangeDealSequenceBatch;
import pg.hl.test.hb.entity.multi.sequence.batch.ExchangeDealSourceSequenceBatchMapper;
import pg.hl.test.hb.entity.multi.sequence.one.ExchangeDealSequenceOne;
import pg.hl.test.hb.entity.multi.sequence.one.ExchangeDealSourceSequenceOneMapper;
import pg.hl.test.hb.entity.simple.identity.SimpleExchangeDealIdentity;
import pg.hl.test.hb.entity.simple.identity.SimpleExchangeDealSourceIdentityMapper;
import pg.hl.test.hb.entity.simple.sequence.batch.SimpleExchangeDealSequenceBatch;
import pg.hl.test.hb.entity.simple.sequence.batch.SimpleExchangeDealSourceSequenceBatchMapper;
import pg.hl.test.hb.entity.simple.sequence.one.SimpleExchangeDealSequenceOne;
import pg.hl.test.hb.entity.simple.sequence.one.SimpleExchangeDealSourceSequenceOneMapper;
import pg.hl.test.sp.bulk.StoredProcedureCopyTestItem;
import pg.hl.test.sp.bulk.multi.BulkUploaderInternal;
import pg.hl.test.sp.bulk.multi.BulkUploaderSource;
import pg.hl.test.sp.bulk.simple.SimpleBulkUploaderInternal;
import pg.hl.test.sp.bulk.simple.SimpleBulkUploaderSource;
import pg.hl.test.sp.ei.multi.ExchangeDealSourceInternalMapper;
import pg.hl.test.sp.ei.simple.SimpleExchangeDealSourceInternalMapper;
import pg.hl.test.sp.json.StoredProcedureJsonTestItem;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
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

    public static Object createPackage(CreatePackageArgument argument) throws SQLException, PropertyVetoException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ExistsDataController existsDataController = ExistsDataController.getOrCreate(argument.getIdentityStrategy(), argument.getEntityType());

        switch (argument.getEntityType()) {
            case Simple:
                var simpleExchangeDealSourceList = EASY_RANDOM
                        .objects(SimpleExchangeDealSource.class, argument.getPackageSize())
                        .peek(exchangeDealSource -> {
                            // Тип
                            exchangeDealSource.setTypeCode(existsDataController.getDealsTypes().getRandomCode());
                        })
                        .collect(Collectors.toList());

                // Устанавливаем имеющиеся GUId
                existsDataController.populateDeals(argument.getPackageSizeExists());
                for (int i = 0; i < argument.getPackageSizeExists(); i++) {
                    simpleExchangeDealSourceList.get(i).setGuid(existsDataController.getDealGuid(i));
                }

                return new SimpleExchangeDealsPackage(simpleExchangeDealSourceList);
            case Multi:
                var exchangeDealSourceList = EASY_RANDOM
                        .objects(ExchangeDealSource.class, argument.getPackageSize())
                        .peek(exchangeDealSource -> {
                            // Тип
                            exchangeDealSource.setTypeCode(existsDataController.getDealsTypes().getRandomCode());

                            // Персоны
                            exchangeDealSource.getPersons().addAll(
                                    EASY_RANDOM.objects(ExchangeDealPersonSource.class, argument.getPersonsSize())
                                            .peek(exchangeDealPersonSource -> exchangeDealPersonSource.setPersonGUId(existsDataController.getPersons().getRandomCode(exchangeDealSource.getGuid())))
                                            .collect(Collectors.toList()));

                            // Статусы
                            AtomicInteger index = new AtomicInteger(1);
                            exchangeDealSource.getStatuses().addAll(
                                    EASY_RANDOM.objects(ExchangeDealStatusSource.class, argument.getStatusesSize())
                                            .peek(exchangeDealStatusSource -> {
                                                exchangeDealStatusSource.setIndex(index.getAndIncrement());
                                                exchangeDealStatusSource.setTypeCode(existsDataController.getStatusesTypes().getRandomCode(exchangeDealSource.getGuid()));
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

    public static DatabaseHelper createDatabaseHelper(IdentityStrategy identityStrategy, EntityType entityType) throws SQLException, PropertyVetoException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return (DatabaseHelper) createTestItem(new CreateTestItemArgument(TestItemsCodes.Hibernate.Min.Before, ResolveStrategy.Cache, identityStrategy, ConnectionPoolType.Hikari, entityType));
    }

    public static TestItem createTestItem(CreateTestItemArgument argument) throws SQLException, PropertyVetoException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        switch (argument.getCode()) {
            case TestItemsCodes.StoredProcedure.Json:
                return createStoredProcedureJsonTestItem(argument);
            case TestItemsCodes.StoredProcedure.Bulk:
                return createStoredProcedureCopyTestItem(argument);

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

    private static TestItem createStoredProcedureJsonTestItem(CreateTestItemArgument argument) throws PropertyVetoException, SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        switch (argument.getEntityType()) {
            case Simple:
                return new StoredProcedureJsonTestItem<>(argument, SimpleExchangeDealsPackage.class, SimpleExchangeDealSourceInternalMapper.class);
            case Multi:
                return new StoredProcedureJsonTestItem<>(argument, ExchangeDealsPackage.class, ExchangeDealSourceInternalMapper.class);
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getEntityType());
        }
    }

    private static TestItem createStoredProcedureCopyTestItem(CreateTestItemArgument argument) throws PropertyVetoException, SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        switch (argument.getEntityType()) {
            case Simple:
                return new StoredProcedureCopyTestItem<>(argument, SimpleExchangeDealsPackage.class, SimpleExchangeDealSourceInternalMapper.class, SimpleBulkUploaderSource.class, SimpleBulkUploaderInternal.class);
            case Multi:
                return new StoredProcedureCopyTestItem<>(argument, ExchangeDealsPackage.class, ExchangeDealSourceInternalMapper.class, BulkUploaderSource.class, BulkUploaderInternal.class);
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getEntityType());
        }
    }

    private static TestItem createHibernateTestItem(CreateHibernateTestItemArgument argument) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        switch (argument.getParentArgument().getEntityType()) {
            case Simple:
                switch (argument.getParentArgument().getIdentityStrategy()) {
                    case Identity:
                        return new HibernateTestItem<>(SimpleExchangeDealsPackage.class, SimpleExchangeDealIdentity.class, SimpleExchangeDealSourceIdentityMapper.class, argument);
                    case SequenceOne:
                        return new HibernateTestItem<>(SimpleExchangeDealsPackage.class, SimpleExchangeDealSequenceOne.class, SimpleExchangeDealSourceSequenceOneMapper.class, argument);
                    case SequenceBatch:
                        return new HibernateTestItem<>(SimpleExchangeDealsPackage.class, SimpleExchangeDealSequenceBatch.class, SimpleExchangeDealSourceSequenceBatchMapper.class, argument);
                    default:
                        throw new IllegalStateException("Unexpected value: " + argument.getParentArgument().getIdentityStrategy());
                }
            case Multi:
                switch (argument.getParentArgument().getIdentityStrategy()) {
                    case Identity:
                        return new HibernateTestItem<>(ExchangeDealsPackage.class, ExchangeDealIdentity.class, ExchangeDealSourceIdentityMapper.class, argument);
                    case SequenceOne:
                        return new HibernateTestItem<>(ExchangeDealsPackage.class, ExchangeDealSequenceOne.class, ExchangeDealSourceSequenceOneMapper.class, argument);
                    case SequenceBatch:
                        return new HibernateTestItem<>(ExchangeDealsPackage.class, ExchangeDealSequenceBatch.class, ExchangeDealSourceSequenceBatchMapper.class, argument);
                    default:
                        throw new IllegalStateException("Unexpected value: " + argument.getParentArgument().getIdentityStrategy());
                }
            default:
                throw new IllegalStateException("Unexpected value: " + argument.getParentArgument().getEntityType());
        }
    }
}

