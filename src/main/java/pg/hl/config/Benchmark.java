package pg.hl.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openjdk.jmh.runner.options.VerboseMode;
import pg.hl.test.*;

import java.util.List;

@Getter
@Setter
@ToString
public class Benchmark {
    private VerboseMode verboseMode;

    private int forkWarmups;
    private int forkValue;
    private int warmupIterations;
    private int measurementIterations;

    private List<TestItemKind> testItemKinds;
    private List<ConnectionPoolType> connectionPoolTypes;
    private List<EntityType> entityTypes;
    private List<ResolveStrategy> resolveStrategies;
    private List<IdentityStrategy> identityStrategies;
    private List<Integer> packageSizes;
    private List<Integer> packagesSizesExists;
}

