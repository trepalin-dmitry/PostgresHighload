package pg.hl.test;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.jpa.ExchangeDeal;
import pg.hl.test.hb.HibernateHikariTestItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TestUtils {
    private static final EasyRandom easyRandom;
    private static final List<UUID> existsDealsGUIds = new ArrayList<>();

    static {
        // Создаем генератор
        EasyRandomParameters easyRandomParameters = new EasyRandomParameters();
        easyRandomParameters.setSeed(new Random().nextLong());
        easyRandom = new EasyRandom(easyRandomParameters);
    }

    public static void prepareExistsGUIds(int size) {
        var gapSize = size - existsDealsGUIds.size();
        if (gapSize > 0) {
            try (var item = new HibernateHikariTestItem()) {
                var deals = item.find(gapSize);
                for (ExchangeDeal deal : deals) {
                    existsDealsGUIds.add(deal.getGuid());
                }
            }
        }
    }

    public static ExchangeDealsPackage createPackage(CreatePackageArgument argument) {
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

        if (argument.getSizeExists() > argument.getSize()) {
            throw new IllegalArgumentException("argument.getSizeExists() > argument.getSize()");
        }

        // Устанавливаем имеющиеся GUId
        for (int i = 0; i < argument.getSizeExists(); i++) {
            exchangeDealSourceList.get(i).setGuid(existsDealsGUIds.get(i));
        }

        return new ExchangeDealsPackage(exchangeDealSourceList);
    }
}

