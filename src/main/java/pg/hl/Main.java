package pg.hl;

import lombok.Getter;
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

import java.beans.PropertyVetoException;
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

    public static void main(String[] args) throws NoSuchFieldException, PropertyVetoException, SQLException, IOException, InterruptedException, RunnerException {
        if (Settings.BenchmarkConstants.Test) {
            for (String testItemsCode : getParamValues("testItemCode", s -> s)) {
                for (ConnectionPoolType connectionPoolType : getParamValues("connectionPoolType", ConnectionPoolType::valueOf)) {
                    for (EntityType entityType : getParamValues("entityType", EntityType::valueOf)) {
                        for (ResolveStrategy resolveStrategy : getParamValues("resolveStrategy", ResolveStrategy::valueOf)) {
                            for (IdentityStrategy identityStrategy : getParamValues("identityStrategy", IdentityStrategy::valueOf)) {
                                for (Integer packageSize : getParamValues("packageSize", Integer::valueOf)) {
                                    for (Integer packageSizeExist : getParamValues("packageSizeExists", Integer::valueOf)) {
                                        for (int i = 0; i < (Settings.BenchmarkConstants.FORK_WARMUPS + Settings.BenchmarkConstants.FORK_VALUE); i++) {
                                            var argument = new UploadDealsArgument();
                                            try {
                                                argument
                                                        .setTestItemCode(testItemsCode)
                                                        .setConnectionPoolType(connectionPoolType)
                                                        .setResolveStrategy(resolveStrategy)
                                                        .setIdentityStrategy(identityStrategy)
                                                        .setPackageSize(packageSize)
                                                        .setPackageSizeExists(packageSizeExist)
                                                        .setEntityType(entityType);

                                                System.out.println("argument = " + argument);
                                                argument.setup();
                                                try {
                                                    for (int j = 0; j < (Settings.BenchmarkConstants.WARMUP_ITERATIONS + Settings.BenchmarkConstants.MEASUREMENT_ITERATIONS); j++) {
                                                        uploadDeals(argument);
                                                    }
                                                } finally {
                                                    argument.tearDown();
                                                }
                                            } finally {
                                                argument.close();
                                            }
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
    public static class UploadDealsArgument implements CreatePackageArgument {
        @ToString.Exclude
        private CreateTestItemArgument argument;
        @ToString.Exclude
        private TestItem testItem;

        @ToString.Exclude
        private final BlockingQueue<RunArgument> runArguments = new LinkedBlockingQueue<>();

        @Getter
        @Setter
        @Param({
                TestItemsCodes.StoredProcedure.Json,
                TestItemsCodes.StoredProcedure.Bulk,

                TestItemsCodes.Hibernate.Min.Before,
                TestItemsCodes.Hibernate.Min.OnException,

                TestItemsCodes.Hibernate.Max.Before,
                TestItemsCodes.Hibernate.Max.OnException,
        })
        private String testItemCode;

        @Getter
        @Setter
        @Param({
                "Hikari",
                "C3p0",
        })
        private ConnectionPoolType connectionPoolType;

        @Getter
        @Setter
        @Param({
//                "Simple",
                "Multi",
        })
        private EntityType entityType;

        @Getter
        @Setter
        @Param({
                "Cache",
                "Database",
        })
        private ResolveStrategy resolveStrategy;

        @Getter
        @Setter
        @Param({
                "Identity",
                "SequenceOne",
                "SequenceBatch",
        })
        private IdentityStrategy identityStrategy;

        @Getter
        @Setter
        @Param({
                "10",
//                "100",
//                "1000",
//                "2000",
        })
        private Integer packageSize;

        @Getter
        @Setter
        @Param({
                "0",
                "1",
                "10"
        })
        private Integer packageSizeExists;

        @Getter
        private final Integer personsSize = Settings.BenchmarkConstants.EXCHANGE_DEALS_PERSONS_SIZE;
        @Getter
        private final Integer statusesSize = Settings.BenchmarkConstants.EXCHANGE_DEALS_STATUSES_SIZE;

        @Setup(Level.Trial)
        public void setup() throws PropertyVetoException, SQLException {
            var argument = new CreateTestItemArgument(this.testItemCode, this.resolveStrategy, this.identityStrategy, this.connectionPoolType);
            if (!Objects.equals(this.argument, argument)) {
                closeTestItem();
                this.testItem = TestUtils.createTestItem(argument);
                this.argument = argument;
            }

            // Подготовка пакетов
            var size = (Settings.BenchmarkConstants.WARMUP_ITERATIONS + Settings.BenchmarkConstants.MEASUREMENT_ITERATIONS);
            for (int i = 0; i < size; i++) {
                runArguments.add(new RunArgument(TestUtils.createPackage(this)));
            }
        }

        @TearDown(Level.Trial)
        public void tearDown() {
        }

        public void run() throws DevException, SQLException, IOException, InterruptedException {
            this.testItem.run(runArguments.take());
        }

        public void close() throws SQLException {
            closeTestItem();
        }

        protected void closeTestItem() throws SQLException {
            if (this.testItem != null) {
                this.testItem.close();
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(warmups = Settings.BenchmarkConstants.FORK_WARMUPS, value = Settings.BenchmarkConstants.FORK_VALUE)
    @Warmup(iterations = Settings.BenchmarkConstants.WARMUP_ITERATIONS, time = 1, timeUnit = TimeUnit.NANOSECONDS)
    @Measurement(iterations = Settings.BenchmarkConstants.MEASUREMENT_ITERATIONS, time = 1, timeUnit = TimeUnit.NANOSECONDS)
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