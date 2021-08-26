package pg.hl.test;

import pg.hl.test.hb.simple.HibernateSimpleTestItem;

import java.util.ArrayList;

public class TestUtils {
    public static TestItems createTestItems() {
        var threadsCounts = new ArrayList<Integer>();
        threadsCounts.add(1);

        var packageSizes = new ArrayList<Integer>();
        packageSizes.add(100);

        var items = new TestItems();
        var index = 1;
        for (Integer threadsCount : threadsCounts) {
            for (Integer packageSize : packageSizes) {
                for (TestType type : TestType.values()) {
                    TestItem item;
                    //noinspection SwitchStatementWithTooFewBranches
                    switch (type) {
                        case HibernateSimple:
                            item = new HibernateSimpleTestItem(threadsCount, packageSize);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + type);
                    }
                    items.put(index++, item);
                }
            }
        }
        return items;
    }
}



