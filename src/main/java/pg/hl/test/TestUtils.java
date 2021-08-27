package pg.hl.test;

import pg.hl.test.hb.simple.HibernateSimpleTestItem;

import java.util.ArrayList;

public class TestUtils {
    public static TestItems createTestItems() {

        var testArguments = new ArrayList<TestArgument>();
        testArguments.add(new TestArgument(1, 1, 5, 5));

        var items = new TestItems();
        var index = 1;
        for (TestArgument testArgument : testArguments) {
            for (TestType type : TestType.values()) {
                TestItem item;
                //noinspection SwitchStatementWithTooFewBranches
                switch (type) {
                    case HibernateSimple:
                        item = new HibernateSimpleTestItem(testArgument);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }
                items.put(index++, item);
            }
        }
        return items;
    }
}



