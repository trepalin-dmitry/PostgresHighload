package pg.hl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.hl.test.TestUtils;

import javax.management.OperationsException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, OperationsException {

        Logger logger = LogManager.getLogger(Main.class);

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
