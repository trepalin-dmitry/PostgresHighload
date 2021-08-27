package pg.hl.test;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.jeasy.random.EasyRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.hl.dto.ExchangeDealPersonSource;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealStatusSource;
import pg.hl.dto.ExchangeDealsPackage;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ToString(onlyExplicitlyIncluded = true)
@Getter
public abstract class AbstractTestItem implements TestItem {
    @ToString.Include
    private final TestType type;
    @ToString.Include
    private final TestArgument params;

    private ExchangeDealsPackage dealsPackage;

    @Getter(AccessLevel.PROTECTED)
    private final Logger logger;

    public AbstractTestItem(TestType type, TestArgument params) {
        logger = LoggerFactory.getLogger(getLoggerName());
        this.type = type;
        this.params = params;
    }

    protected abstract String getLoggerName();

    public void createPackage() {
        EasyRandom easyRandom = new EasyRandom();
        var exchangeDealSourceList = easyRandom
                .objects(ExchangeDealSource.class, params.getPackageSize())
                .peek(exchangeDealSource -> exchangeDealSource.getPersons().addAll(
                        easyRandom.objects(ExchangeDealPersonSource.class, params.getExchangeDealsPersonsSize())
                                .collect(Collectors.toList())))
                .peek(exchangeDealSource -> {
                    AtomicInteger index = new AtomicInteger(1);
                    exchangeDealSource.getStatuses().addAll(
                            easyRandom.objects(ExchangeDealStatusSource.class, params.getExchangeDealsStatusesSize())
                                    .peek(exchangeDealStatusSource -> exchangeDealStatusSource.setIndex(index.getAndIncrement()))
                                    .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());
        dealsPackage = new ExchangeDealsPackage(exchangeDealSourceList);
    }

    public void run() throws InvocationTargetException, IllegalAccessException {
        getLogger().info("test run start");
        runInternal(new RunArgument(dealsPackage));
        getLogger().info("test run finish");
    }

    protected abstract void runInternal(RunArgument argument) throws InvocationTargetException, IllegalAccessException;
}

