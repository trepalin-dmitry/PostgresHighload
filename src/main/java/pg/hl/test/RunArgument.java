package pg.hl.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import pg.hl.dto.ExchangeDealsPackage;

@ToString
@Getter
@AllArgsConstructor
public class RunArgument {
    @ToString.Exclude
    private final ExchangeDealsPackage dealsPackage;
}
