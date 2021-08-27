package pg.hl.test;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(onlyExplicitlyIncluded = true)
public class TestArgument {
    @ToString.Include
    private final int packagesCount;
    @ToString.Include
    private final int packageSize;
    @ToString.Include
    private final int exchangeDealsPersonsSize;
    @ToString.Include
    private final int exchangeDealsStatusesSize;

    public TestArgument(int packagesCount, int packageSize, int exchangeDealsPersonsSize, int exchangeDealsStatusesSize) {
        this.packagesCount = packagesCount;
        this.packageSize = packageSize;
        this.exchangeDealsPersonsSize = exchangeDealsPersonsSize;
        this.exchangeDealsStatusesSize = exchangeDealsStatusesSize;
    }
}
