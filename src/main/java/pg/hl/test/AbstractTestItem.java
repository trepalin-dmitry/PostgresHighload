package pg.hl.test;

import lombok.Getter;
import lombok.ToString;
import org.jeasy.random.EasyRandom;
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
    private final String caption;
    @ToString.Include
    private final TestArgument argument;

    private ExchangeDealsPackage dealsPackage;

    public AbstractTestItem(String caption, TestArgument params) {
        this.caption = caption;
        this.argument = params;
    }

    public void createPackage() {
        EasyRandom easyRandom = new EasyRandom();
        var exchangeDealSourceList = easyRandom
                .objects(ExchangeDealSource.class, argument.getPackageSize())
                .peek(exchangeDealSource -> exchangeDealSource.getPersons().addAll(
                        easyRandom.objects(ExchangeDealPersonSource.class, argument.getExchangeDealsPersonsSize())
                                .collect(Collectors.toList())))
                .peek(exchangeDealSource -> {
                    AtomicInteger index = new AtomicInteger(1);
                    exchangeDealSource.getStatuses().addAll(
                            easyRandom.objects(ExchangeDealStatusSource.class, argument.getExchangeDealsStatusesSize())
                                    .peek(exchangeDealStatusSource -> exchangeDealStatusSource.setIndex(index.getAndIncrement()))
                                    .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());
        dealsPackage = new ExchangeDealsPackage(exchangeDealSourceList);
    }

    public void run() throws InvocationTargetException, IllegalAccessException {
        runInternal(new RunArgument(dealsPackage));
    }

    protected abstract void runInternal(RunArgument argument) throws InvocationTargetException, IllegalAccessException;
}

