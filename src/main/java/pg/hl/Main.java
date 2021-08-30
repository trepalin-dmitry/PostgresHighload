package pg.hl;

import lombok.Getter;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pg.hl.test.CreatePackageArgument;
import pg.hl.test.RunArgument;
import pg.hl.test.TestItem;
import pg.hl.test.TestUtils;
import pg.hl.test.hb.simple.HibernateC3p0TestItem;
import pg.hl.test.hb.simple.HibernateHikariTestItem;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final class BenchmarkConstants {
        public static final class Fork {
            public final static int Value = 1;
            public final static int Warmups = 1;
        }

        public static final class Warmup {
            public final static int Iterations = 1;
        }

        public static final class Measurement {
            public final static int Iterations = 1;
        }
    }

    static final class TestItemMode {
        public static final String HibernateHikariTestItem = "HibernateHikariTestItem";
        public static final String HibernateC3p0TestItem = "HibernateC3p0TestItem";
    }

    @State(Scope.Thread)
    public static class UploadDealsArgument {

        private final Queue<RunArgument> dealsPackages = new LinkedList<>();

        @Param({TestItemMode.HibernateHikariTestItem, TestItemMode.HibernateC3p0TestItem})
        @Getter
        private String testItemMode;
        @Param({"10"})
        private int packageSize;
        @Param({"5"})
        private int exchangeDealsPersonsSize;
        @Param({"5"})
        private int exchangeDealsStatusesSize;

        @Setup(Level.Trial)
        public void setup() {
            // Подготовка пакетов
            var size = (BenchmarkConstants.Fork.Warmups + BenchmarkConstants.Fork.Value) * (BenchmarkConstants.Warmup.Iterations + BenchmarkConstants.Measurement.Iterations);
            for (int i = 0; i < size; i++) {
                dealsPackages.add(new RunArgument(TestUtils.createPackage(new CreatePackageArgument(packageSize, exchangeDealsPersonsSize, exchangeDealsStatusesSize))));
            }
        }

        @TearDown
        public void tearDown() {
        }

        public RunArgument peekRunArgument() throws Exception {
            var result = dealsPackages.peek();
            if (result == null) {
                throw new Exception("Данных нет");
            }
            return result;
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(warmups = BenchmarkConstants.Fork.Warmups, value = BenchmarkConstants.Fork.Value)
    @Warmup(iterations = BenchmarkConstants.Warmup.Iterations, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = BenchmarkConstants.Measurement.Iterations, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    public void uploadDeals(UploadDealsArgument uploadDealsArgument) throws Exception {
        TestItem testItem;

        switch (uploadDealsArgument.getTestItemMode()) {
            case TestItemMode.HibernateHikariTestItem:
                testItem = new HibernateHikariTestItem();
                break;
            case TestItemMode.HibernateC3p0TestItem:
                testItem = new HibernateC3p0TestItem();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + uploadDealsArgument.getTestItemMode());
        }

        RunArgument runArgument = uploadDealsArgument.peekRunArgument();
        testItem.run(runArgument);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Main.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
