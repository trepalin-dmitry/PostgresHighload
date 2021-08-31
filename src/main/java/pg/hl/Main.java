package pg.hl;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pg.hl.test.*;
import pg.hl.test.hb.HibernateC3p0TestItem;
import pg.hl.test.hb.HibernateHikariTestItem;
import pg.hl.test.sp.StoredProcedureTestItem;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final class BenchmarkConstants {
        public static final class Fork {
            public final static int Value = 2;
            public final static int Warmups = 1;
        }
    }

    @State(Scope.Thread)
    public static class UploadDealsArgument {
        private final static Map<String, TestItem> testItems = new HashMap<>();
        private final Queue<RunArgument> runArguments = new LinkedList<>();

        @Param({TestItemsCodes.HibernateHikari, TestItemsCodes.HibernateC3p0, TestItemsCodes.StoredProcedure})
        private String testItemCode;
        @Param("1000")
        private int packageSize;
        @Param("1")
        private int packageSizeExists;
        @Param({"5"})
        private int exchangeDealsPersonsSize;
        @Param({"5"})
        private int exchangeDealsStatusesSize;

        @Setup(Level.Trial)
        public void setup() {
            // Подготовка GUID
            TestUtils.prepareExistsGUIds(packageSizeExists);

            // Подготовка пакетов
            var size = (BenchmarkConstants.Fork.Warmups + BenchmarkConstants.Fork.Value);
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
                TestItem testItem;
                switch (code) {
                    case TestItemsCodes.HibernateHikari:
                        testItem = new HibernateHikariTestItem();
                        break;
                    case TestItemsCodes.HibernateC3p0:
                        testItem = new HibernateC3p0TestItem();
                        break;
                    case TestItemsCodes.StoredProcedure:
                        testItem = new StoredProcedureTestItem();
                        break;
                    default:
                        throw new IllegalArgumentException("testItemMode = " + code);
                }
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
    @Fork(warmups = BenchmarkConstants.Fork.Warmups, value = BenchmarkConstants.Fork.Value)
    @Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.MILLISECONDS) // Не изменять iterations - последует повтор аргумента
    @Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.MILLISECONDS) // Не изменять iterations - последует повтор аргумента
    public void uploadDeals(UploadDealsArgument uploadDealsArgument) throws Exception {
        uploadDealsArgument.run();
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
}
