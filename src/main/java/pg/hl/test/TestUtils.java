package pg.hl.test;

import org.jeasy.random.EasyRandom;
import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.dto.ExchangeDealsPackage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TestUtils {
    public static ExchangeDealsPackage createPackage(CreatePackageArgument argument) {
        EasyRandom easyRandom = new EasyRandom();
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
        return new ExchangeDealsPackage(exchangeDealSourceList);
    }
}

