package pg.hl.test;

import lombok.Getter;
import pg.hl.dto.ExchangeDealsPackage;

public class RunArgument {
    @Getter
    private final ExchangeDealsPackage dealsPackage;

    public RunArgument(ExchangeDealsPackage dealsPackage) {
        this.dealsPackage = dealsPackage;
    }
}
