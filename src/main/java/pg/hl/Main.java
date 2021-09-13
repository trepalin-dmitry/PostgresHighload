package pg.hl;

import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pg.hl.test.*;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static final class BenchmarkConstants {
        public static boolean Test = false;

        public final static int FORK_WARMUPS = 1;
        public final static int FORK_VALUE = 1;
        public final static int WARMUP_ITERATIONS = 10;
        public final static int MEASUREMENT_ITERATIONS = 10;

        public static final Integer EXCHANGE_DEALS_PERSONS_SIZE = 5;
        public static final Integer EXCHANGE_DEALS_STATUSES_SIZE = 5;
    }

    public static void main(String[] args) throws IOException, RunnerException, SQLException, DevException, NoSuchFieldException, InterruptedException {
        if (BenchmarkConstants.Test) {
            for (String testItemsCode : getParamValues("testItemCode", s -> s)) {
                for (ResolveStrategy resolveStrategy : getParamValues("resolveStrategy", ResolveStrategy::valueOf)) {
                    for (IdentityStrategy identityStrategy : getParamValues("identityStrategy", IdentityStrategy::valueOf)) {
                        for (Integer packageSize : getParamValues("packageSize", Integer::valueOf)) {
                            for (Integer packageSizeExist : getParamValues("packageSizeExists", Integer::valueOf)) {
                                for (int i = 0; i < (BenchmarkConstants.FORK_WARMUPS + BenchmarkConstants.FORK_VALUE); i++) {
                                    try (var argument = new UploadDealsArgument()
                                            .setTestItemCode(testItemsCode)
                                            .setResolveStrategy(resolveStrategy)
                                            .setIdentityStrategy(identityStrategy)
                                            .setPackageSize(packageSize)
                                            .setPackageSizeExists(packageSizeExist)) {
                                        System.out.println("argument = " + argument);
                                        argument.setup();
                                        try {
                                            for (int j = 0; j < (BenchmarkConstants.WARMUP_ITERATIONS + BenchmarkConstants.MEASUREMENT_ITERATIONS); j++) {
                                                uploadDeals(argument);
                                            }
                                        } finally {
                                            argument.tearDown();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Options options = new OptionsBuilder()
                    .include(Main.class.getSimpleName())
                    .resultFormat(ResultFormatType.CSV)
                    .result("C:\\Work\\PostgresHighLoad\\result.csv")
                    .build();
            new Runner(options).run();
        }
    }

    @State(Scope.Benchmark)
    @Accessors(chain = true)
    @ToString
    public static class UploadDealsArgument implements Closeable {
        @ToString.Exclude
        private CreateTestItemArgument argument;
        @ToString.Exclude
        private TestItem testItem;

        @ToString.Exclude
        private final BlockingQueue<RunArgument> runArguments = new LinkedBlockingQueue<>();

        @Setter
        @Param({
                TestItemsCodes.StoredProcedure.Json,
                TestItemsCodes.StoredProcedure.Bulk,

                TestItemsCodes.Hibernate.C3p0.Min.Before,
                TestItemsCodes.Hibernate.C3p0.Min.OnException,

                TestItemsCodes.Hibernate.C3p0.Max.Before,
                TestItemsCodes.Hibernate.C3p0.Max.OnException,

                TestItemsCodes.Hibernate.Hikari.Min.Before,
                TestItemsCodes.Hibernate.Hikari.Min.OnException,

                TestItemsCodes.Hibernate.Hikari.Max.Before,
                TestItemsCodes.Hibernate.Hikari.Max.OnException
        })
        private String testItemCode;

        @Setter
        @Param({
                "Cache",
                "Database",
        })
        private ResolveStrategy resolveStrategy;

        @Setter
        @Param({
                "Identity",
                "SequenceOne",
                "SequenceBatch",
        })
        private IdentityStrategy identityStrategy;

        @Setter
        @Param({
                "10",
                "100",
                "1000",
                "2000",
        })
        private Integer packageSize;

        @Setter
        @Param({
                "0",
                "1",
                "10"
        })
        private Integer packageSizeExists;

        @Setup(Level.Trial)
        public void setup() throws DevException, SQLException, IOException {
            var argument = new CreateTestItemArgument(this.testItemCode, this.resolveStrategy, this.identityStrategy);
            if (!Objects.equals(this.argument, argument)){
                closeTestItem();
                this.testItem = TestUtils.createTestItem(argument);
                this.argument = argument;
            }

            // Подготовка пакетов
            var size = (Main.BenchmarkConstants.WARMUP_ITERATIONS + Main.BenchmarkConstants.MEASUREMENT_ITERATIONS);
            var createPackageArgument = new CreatePackageArgument(this.packageSize, this.packageSizeExists, Main.BenchmarkConstants.EXCHANGE_DEALS_PERSONS_SIZE, Main.BenchmarkConstants.EXCHANGE_DEALS_STATUSES_SIZE, this.identityStrategy);
            for (int i = 0; i < size; i++) {
                runArguments.add(new RunArgument(TestUtils.createPackage(createPackageArgument)));
            }
        }

        @TearDown(Level.Trial)
        public void tearDown() {
        }

        public void run() throws DevException, SQLException, IOException, InterruptedException {
            this.testItem.run(runArguments.take());
        }

        @Override
        public void close() throws IOException {
            closeTestItem();
        }

        protected void closeTestItem() throws IOException {
            if (this.testItem != null) {
                this.testItem.close();
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(warmups = BenchmarkConstants.FORK_WARMUPS, value = BenchmarkConstants.FORK_VALUE)
    @Warmup(iterations = BenchmarkConstants.WARMUP_ITERATIONS, time = 1, timeUnit = TimeUnit.NANOSECONDS)
    @Measurement(iterations = BenchmarkConstants.MEASUREMENT_ITERATIONS, time = 1, timeUnit = TimeUnit.NANOSECONDS)
    public static void uploadDeals(UploadDealsArgument uploadDealsArgument) throws SQLException, DevException, IOException, InterruptedException {
        uploadDealsArgument.run();
    }

    private static <T> Collection<T> getParamValues(String fieldName, Function<String, T> function) throws NoSuchFieldException {
        return Arrays
                .stream(UploadDealsArgument.class
                        .getDeclaredField(fieldName)
                        .getAnnotation(Param.class)
                        .value()
                )
                .map(function)
                .collect(Collectors.toList());
    }
}