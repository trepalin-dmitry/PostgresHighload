package pg.hl.test;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jeasy.random.EasyRandom;
import pg.hl.dto.ExchangeDealSource;
import pg.hl.dto.ExchangeDealsPackage;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

@ToString(onlyExplicitlyIncluded = true)
@Getter
public abstract class AbstractTestItem implements TestItem {
    @ToString.Include
    private final TestType type;
    @ToString.Include
    private final int threadsCount;
    @ToString.Include
    private final int packageSize;

    private ExchangeDealsPackage dealsPackage;

    @Getter(AccessLevel.PROTECTED)
    private final Logger logger;

    public AbstractTestItem(TestType type, int threadsCount, int packageSize) {
        logger = LogManager.getLogger(getLoggerName());

        this.type = type;
        this.threadsCount = threadsCount;
        this.packageSize = packageSize;
    }

    protected abstract String getLoggerName();

    public void createPackage() {
        EasyRandom easyRandom = new EasyRandom();
        var stream = easyRandom.objects(ExchangeDealSource.class, packageSize);
        dealsPackage = new ExchangeDealsPackage(stream.collect(Collectors.toList()));
    }

    public void run() throws InvocationTargetException, IllegalAccessException {
        getLogger().info("test run start");
        runInternal(new RunArgument(dealsPackage));
        getLogger().info("test run finish");
    }

    protected abstract void runInternal(RunArgument argument) throws InvocationTargetException, IllegalAccessException;
}

