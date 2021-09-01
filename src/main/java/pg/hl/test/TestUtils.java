package pg.hl.test;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.jpa.ExchangeDeal;
import pg.hl.test.hb.ConnectionPoolType;
import pg.hl.test.hb.HibernateTestItem;
import pg.hl.test.sp.StoredProcedureTestItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TestUtils {
    private static final EasyRandom easyRandom;
    private static final List<UUID> existsDealsGUIds = new ArrayList<>();

    public static void main(String[] args)
    {
        try {
            createTestItem(TestItemsCodes.HibernateHikariBatch)
                    .run(new RunArgument(TestUtils
                            .createPackage(new CreatePackageArgument(1000, 2, 5, 5))));
        } catch (ProxyException | SQLException e) {
            e.printStackTrace();
        }
    }

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
                                .collect(Collectors.toList())))
                .peek(exchangeDealSource -> {
                    AtomicInteger index = new AtomicInteger(1);
                    exchangeDealSource.getStatuses().addAll(
                            easyRandom.objects(ExchangeDealStatusSource.class, argument.getStatusesSize())
                                    .peek(exchangeDealStatusSource -> exchangeDealStatusSource.setIndex(index.getAndIncrement()))
                                    .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());

        // Устанавливаем имеющиеся GUId
        var gapSize = argument.getSizeExists() - existsDealsGUIds.size();
        if (gapSize > 0) {
            try (var item = (HibernateTestItem) createTestItem(TestItemsCodes.HibernateHikariBatch)) {
                var deals = item.find(gapSize);
                for (ExchangeDeal deal : deals) {
                    existsDealsGUIds.add(deal.getGuid());
                }
            }
        }

        for (int i = 0; i < argument.getSizeExists(); i++) {
            exchangeDealSourceList.get(i).setGuid(existsDealsGUIds.get(i));
        }

        return new ExchangeDealsPackage(exchangeDealSourceList);
    }

    public static TestItem createTestItem(String code) throws SQLException {
        switch (code) {
            case TestItemsCodes.StoredProcedure:
                return new StoredProcedureTestItem();
            case TestItemsCodes.HibernateC3p0:
                return new HibernateTestItem(false, ConnectionPoolType.C3p0);
            case TestItemsCodes.HibernateC3p0Batch:
                return new HibernateTestItem(true, ConnectionPoolType.C3p0);
            case TestItemsCodes.HibernateHikari:
                return new HibernateTestItem(false, ConnectionPoolType.Hikari);
            case TestItemsCodes.HibernateHikariBatch:
                return new HibernateTestItem(true, ConnectionPoolType.Hikari);
            default:
                throw new IllegalStateException("Unexpected value: " + code);
        }
    }
}