package pg.hl.test.hb;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BatchSize {
    Min(5),
    Max(30);

    private final int value;
}
