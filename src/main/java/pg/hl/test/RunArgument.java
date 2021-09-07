package pg.hl.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import pg.hl.dto.ExchangeDealsPackage;
import pg.hl.test.hb.IdentityStrategy;

@ToString
@Getter
@AllArgsConstructor
public class RunArgument {
    @ToString.Exclude
    private final ExchangeDealsPackage dealsPackage;
    private final IdentityStrategy identityStrategy;
}
