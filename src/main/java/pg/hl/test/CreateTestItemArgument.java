package pg.hl.test;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
@ToString
public class CreateTestItemArgument {
    private String code;
    private ResolveStrategy resolveStrategy;
    private IdentityStrategy identityStrategy;
    private ConnectionPoolType connectionPoolType;
}
