package pg.hl;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
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
        public final static int ForkWarmups = 0;
        public final static int ForkValue = 1;
        public final static int WarmupIterations = 1;
        public final static int MeasurementIterations = 1;
    }

    public static void main(String[] args) throws Exception {
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
        private final static Map<CreateTestItemArgument, TestItem> testItems = new HashMap<>();
        private final Queue<RunArgument> runArguments = new LinkedList<>();

        public UploadDealsArgument(){
            System.out.println("public UploadDealsArgument()");
        }

        @Param({
                TestItemsCodes.Hibernate.Hikari.Each,
                TestItemsCodes.Hibernate.Hikari.Batch.Check,
                TestItemsCodes.Hibernate.Hikari.Batch.NoCheck,

                TestItemsCodes.Hibernate.C3p0.Each,
                TestItemsCodes.Hibernate.C3p0.Batch.Check,
                TestItemsCodes.Hibernate.C3p0.Batch.NoCheck,

                TestItemsCodes.StoredProcedure,
        })
        private String testItemCode;
        @Param({"Cache", "Database"})
        private ResolveStrategy resolveStrategy;
        @Param("1")
        private int packageSize;
        @Param({"0", "1"})
        private int packageSizeExists;
        @Param({"5"})
        private int exchangeDealsPersonsSize;
        @Param({"5"})
        private int exchangeDealsStatusesSize;

        @Setup(Level.Trial)
        public void setup() throws Exception {
            ExistsDataController.init();

            // Подготовка пакетов
            @SuppressWarnings("PointlessArithmeticExpression")
            var size = (BenchmarkConstants.ForkWarmups + BenchmarkConstants.ForkValue)
                    * (BenchmarkConstants.WarmupIterations + BenchmarkConstants.MeasurementIterations);
            var createPackageArgument = new CreatePackageArgument(packageSize, packageSizeExists, exchangeDealsPersonsSize, exchangeDealsStatusesSize);
            for (int i = 0; i < size; i++) {
                runArguments.add(new RunArgument(TestUtils.createPackage(createPackageArgument)));
            }
        }

        @TearDown
        public void tearDown() {
        }

        private TestItem getOrCreate(CreateTestItemArgument argument) throws SQLException {
            // Подготовка элемента
            if (!testItems.containsKey(argument)) {
                TestItem testItem = TestUtils.createTestItem(argument);
                testItems.put(argument, testItem);
            }
            return testItems.get(argument);
        }

        public void run() throws Exception {
            var testItem = getOrCreate(new CreateTestItemArgument(testItemCode, resolveStrategy));
            if (testItem == null) {
                throw new Exception("testItem == null");
            }

            var runArgument = runArguments.poll();
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
    @Warmup(iterations = BenchmarkConstants.WarmupIterations, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = BenchmarkConstants.MeasurementIterations, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    public void uploadDeals(UploadDealsArgument uploadDealsArgument) throws Exception {
        uploadDealsArgument.run();
    }
}
