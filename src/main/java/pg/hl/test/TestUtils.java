package pg.hl.test;

import pg.hl.test.hb.simple.HibernateC3p0TestItem;
import pg.hl.test.hb.simple.HibernateHikariTestItem;

import java.util.ArrayList;

public class TestUtils {
    public static TestItems createTestItems() {

        var testArguments = new ArrayList<TestArgument>();
        testArguments.add(new TestArgument(1, 1, 5, 5));

        var items = new TestItems();
        var index = 1;
        for (TestArgument testArgument : testArguments) {
            var currentItems = createTestItems(testArgument);
            for (TestItem currentItem : currentItems) {
                items.put(index++, currentItem);
            }
        }
        return items;
    }

    private static TestItem[] createTestItems(TestArgument testArgument) {
        return new TestItem[]{
                new HibernateHikariTestItem(testArgument),
                new HibernateC3p0TestItem(testArgument)
        };
    }
}



