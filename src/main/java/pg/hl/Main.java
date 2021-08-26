package pg.hl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.hl.test.TestUtils;

import java.lang.reflect.InvocationTargetException;

public class Main {

    static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        var testItems = TestUtils.createTestItems();

        for (Integer index : testItems.keySet()) {
            var item = testItems.get(index);

            logger.info("cleanDatabase start");
            item.cleanDatabase();
            logger.info("cleanDatabase finish");

            logger.info("generate package start");
            item.createPackage();
            logger.info("generate package finish");

            logger.info("test start, {}, {}", index, item);
            item.run();
            logger.info("test finish, {}, {}", index, item);
        }
    }
}
