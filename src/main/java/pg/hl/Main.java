package pg.hl;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pg.hl.test.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final class BenchmarkConstants {
        public final static int ForkWarmups = 1;
        public final static int ForkValue = 10;
    }

    public static void main(String[] args) throws RunnerException, IOException {
        try {
            Options options = new OptionsBuilder()
                    .include(Main.class.getSimpleName())
                    .build();
            new Runner(options).run();
        } finally {
            UploadDealsArgument.closeItems();
        }
    }

    @State(Scope.Thread)
    public static class UploadDealsArgument {
        private final static Map<String, TestItem> testItems = new HashMap<>();
        private final Queue<RunArgument> runArguments = new LinkedList<>();

        @Param({
                //TestItemsCodes.HibernateHikariEach,
                TestItemsCodes.HibernateHikariBatchCheckExistsBefore,
                TestItemsCodes.HibernateHikariBatchHandleException,

                //TestItemsCodes.HibernateC3p0Each,
                TestItemsCodes.HibernateC3p0BatchCheckExistsBefore,
                TestItemsCodes.HibernateC3p0BatchHandleException,

                TestItemsCodes.StoredProcedure
        })
        private String testItemCode;
        @Param("1000")
        private int packageSize;
        @Param({"0", "1"})
        private int packageSizeExists;
        @Param({"5"})
        private int exchangeDealsPersonsSize;
        @Param({"5"})
        private int exchangeDealsStatusesSize;

        @Setup(Level.Trial)
        public void setup() throws SQLException {
            // Подготовка пакетов
            var size = (BenchmarkConstants.ForkWarmups + BenchmarkConstants.ForkValue);

            for (int i = 0; i < size; i++) {
                runArguments.add(new RunArgument(TestUtils.createPackage(new CreatePackageArgument(packageSize, packageSizeExists, exchangeDealsPersonsSize, exchangeDealsStatusesSize))));
            }
        }

        @TearDown
        public void tearDown() {
        }

        public TestItem getOrCreate(String code) throws SQLException {
            // Подготовка элемента
            if (!testItems.containsKey(code)) {
                TestItem testItem = TestUtils.createTestItem(code);
                testItems.put(code, testItem);
            }
            return testItems.get(code);
        }

        public void run() throws Exception {
            var testItem = getOrCreate(testItemCode);
            if (testItem == null) {
                throw new Exception("testItem == null");
            }

            var runArgument = runArguments.peek();
            if (runArgument == null) {
                throw new Exception("runArgument == null");
            }

            testItem.run(runArgument);
        }

        public static void closeItems() throws IOException {
            for (TestItem value : testItems.values()) {
                value.close();
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(warmups = BenchmarkConstants.ForkWarmups, value = BenchmarkConstants.ForkValue)
    @Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    public void uploadDeals(UploadDealsArgument uploadDealsArgument) throws Exception {
        uploadDealsArgument.run();
    }
}
