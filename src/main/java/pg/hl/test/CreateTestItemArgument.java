package pg.hl.test;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class CreateTestItemArgument {
    private String code;
    private ResolveStrategy resolveStrategy;
}
