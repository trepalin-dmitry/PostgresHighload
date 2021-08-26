package pg.hl.test;

import pg.hl.test.hb.simple.HibernateSimpleTestItem;

import java.util.ArrayList;

public class TestUtils {
    public static TestItems createTestItems() {
        var threadsCounts = new ArrayList<Integer>();
        threadsCounts.add(1);

        var exchangeDealsSizes = new ArrayList<Integer>();
        exchangeDealsSizes.add(100);

        var exchangeDealsPersonsSizes = new ArrayList<Integer>();
        exchangeDealsPersonsSizes.add(5);

        var exchangeDealsStatusesSizes = new ArrayList<Integer>();
        exchangeDealsStatusesSizes.add(5);

        var items = new TestItems();
        var index = 1;
        for (Integer threadsCount : threadsCounts) {
            for (Integer exchangeDealsSize : exchangeDealsSizes) {
                for (Integer exchangeDealsPersonsSize : exchangeDealsPersonsSizes) {
                    for (Integer exchangeDealsStatusesSize : exchangeDealsStatusesSizes) {
                        for (TestType type : TestType.values()) {
                            TestItem item;
                            //noinspection SwitchStatementWithTooFewBranches
                            switch (type) {
                                case HibernateSimple:
                                    item = new HibernateSimpleTestItem(threadsCount, exchangeDealsSize, exchangeDealsPersonsSize, exchangeDealsStatusesSize);
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + type);
                            }
                            items.put(index++, item);
                        }
                    }
                }
            }
        }
        return items;
    }
}



