package pg.hl.test;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pg.hl.test.hb.IdentityStrategy;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class CreateTestItemArgument {
    private String code;
    private ResolveStrategy resolveStrategy;
    private IdentityStrategy identityStrategy;
}
