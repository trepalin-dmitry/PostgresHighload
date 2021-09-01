package pg.hl.test;

import java.io.Closeable;

public interface TestItem extends Closeable {
    void run(RunArgument runArgument) throws ProxyException;
}

