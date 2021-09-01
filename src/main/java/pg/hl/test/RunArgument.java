package pg.hl.test;

import lombok.Getter;
import lombok.ToString;
import pg.hl.dto.ExchangeDealsPackage;

@ToString
public class RunArgument {
    @Getter
    private final ExchangeDealsPackage dealsPackage;

    public RunArgument(ExchangeDealsPackage dealsPackage) {
        this.dealsPackage = dealsPackage;
    }
}
