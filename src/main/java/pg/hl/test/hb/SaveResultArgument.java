package pg.hl.test.hb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openjdk.jmh.results.RunResult;

import java.time.LocalDateTime;
import java.util.Collection;

@AllArgsConstructor
@Getter
public class SaveResultArgument {
    private LocalDateTime startDateTime;
    private LocalDateTime finishDateTime;
    private String configuration;
    private Collection<RunResult> runResults;
}
