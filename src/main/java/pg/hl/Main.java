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
import org.openjdk.jmh.runner.options.TimeValue;
import pg.hl.config.Configuration;
import pg.hl.test.*;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) throws PropertyVetoException, SQLException, IOException, InterruptedException, RunnerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Configuration configuration = Configuration.getInstance();

        switch (configuration.getStartup().getMode()) {

            case Run:

                for (TestItemKind testItemKind : configuration.getBenchmark().getTestItemKinds()) {
                    for (ConnectionPoolType connectionPoolType : configuration.getBenchmark().getConnectionPoolTypes()) {
                        for (EntityType entityType : configuration.getBenchmark().getEntityTypes()) {
                            for (ResolveStrategy resolveStrategy : configuration.getBenchmark().getResolveStrategies()) {
                                for (IdentityStrategy identityStrategy : configuration.getBenchmark().getIdentityStrategies()) {
                                    for (Integer packageSize : configuration.getBenchmark().getPackageSizes()) {
                                        for (Integer packageSizeExist : configuration.getBenchmark().getPackagesSizesExists()) {
                                            for (int i = 0; i < (configuration.getBenchmark().getForkValue() + configuration.getBenchmark().getForkValue()); i++) {
                                                var argument = new UploadDealsArgument();
                                                try {
                                                    argument
                                                            .setTestItemKind(testItemKind)
                                                            .setConnectionPoolType(connectionPoolType)
                                                            .setResolveStrategy(resolveStrategy)
                                                            .setIdentityStrategy(identityStrategy)
                                                            .setPackageSize(packageSize)
                                                            .setPackageSizeExists(packageSizeExist)
                                                            .setEntityType(entityType);

                                                    System.out.println("argument = " + argument);
                                                    argument.setup();
                                                    try {
                                                        for (int j = 0; j < (configuration.getBenchmark().getWarmupIterations() + configuration.getBenchmark().getMeasurementIterations()); j++) {
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

                break;
            case Benchmark:

                Options options = new OptionsBuilder()
                        .include(Main.class.getSimpleName())

                        .resultFormat(ResultFormatType.CSV)
                        .result(configuration.getBenchmark().getResultFilePath())

                        .forks(configuration.getBenchmark().getForkValue())
                        .warmupForks(configuration.getBenchmark().getForkWarmups())
                        .warmupIterations(configuration.getBenchmark().getWarmupIterations())
                        .measurementIterations(configuration.getBenchmark().getMeasurementIterations())
                        .warmupTime(TimeValue.nanoseconds(1))
                        .measurementTime(TimeValue.nanoseconds(1))

                        .param("testItemCode", toStringArray(configuration.getBenchmark().getTestItemKinds(), TestItemKind::name))
                        .param("connectionPoolType", toStringArray(configuration.getBenchmark().getConnectionPoolTypes(), ConnectionPoolType::name))
                        .param("entityType", toStringArray(configuration.getBenchmark().getEntityTypes(), EntityType::name))
                        .param("resolveStrategy", toStringArray(configuration.getBenchmark().getResolveStrategies(), ResolveStrategy::name))
                        .param("identityStrategy", toStringArray(configuration.getBenchmark().getIdentityStrategies(), IdentityStrategy::name))
                        .param("packageSize", toStringArray(configuration.getBenchmark().getPackageSizes(), s -> Integer.toString(s)))
                        .param("packageSizeExists", toStringArray(configuration.getBenchmark().getPackagesSizesExists(), s -> Integer.toString(s)))

                        .build();
                new Runner(options).run();

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + configuration.getStartup().getMode());
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
        @Param("StoredProcedureJson")
        private TestItemKind testItemKind;

        @Getter
        @Setter
        @Param("Hikari")
        private ConnectionPoolType connectionPoolType;

        @Getter
        @Setter
        @Param("Simple")
        private EntityType entityType;

        @Getter
        @Setter
        @Param("Cache")
        private ResolveStrategy resolveStrategy;

        @Getter
        @Setter
        @Param("Identity")
        private IdentityStrategy identityStrategy;

        @Getter
        @Setter
        @Param("1")
        private Integer packageSize;

        @Getter
        @Setter
        @Param("0")
        private Integer packageSizeExists;

        public UploadDealsArgument() {
        }

        @Setup(Level.Trial)
        public void setup() throws PropertyVetoException, SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
            var argument = new CreateTestItemArgument(this.testItemKind, this.resolveStrategy, this.identityStrategy, this.connectionPoolType, this.entityType);
            if (!Objects.equals(this.argument, argument)) {
                closeTestItem();
                this.testItem = TestUtils.createTestItem(argument);
                this.argument = argument;
            }

            // Подготовка пакетов
            var size = (Configuration.getInstance().getBenchmark().getWarmupIterations() + Configuration.getInstance().getBenchmark().getMeasurementIterations());
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
    public static void uploadDeals(UploadDealsArgument uploadDealsArgument) throws SQLException, DevException, IOException, InterruptedException {
        uploadDealsArgument.run();
    }

    private static <T> String[] toStringArray(Collection<T> items, Function<T, String> toStringFunction) {
        return items.stream().map(toStringFunction).toArray(String[]::new);
    }
}