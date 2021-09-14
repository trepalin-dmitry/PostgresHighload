package pg.hl.test.edc;

import lombok.Getter;
import pg.hl.DevException;
import pg.hl.test.IdentityStrategy;
import pg.hl.test.TestUtils;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.*;

public class ExistsDataController {
    private static final Integer PERSONS_SIZE = 1000;
    private static final Integer STATUSES_SIZE = 100;
    private static final Integer TYPES_SIZE = 100;

    private static final Map<IdentityStrategy, ExistsDataController> controllers = new HashMap<>();
    private final IdentityStrategy identityStrategy;
    private final List<UUID> existsDealsGUIds = new ArrayList<>();

    @Getter
    private final PersonsExistsDataItem persons;
    @Getter
    private final StatusesTypesExistsDataItem statusesTypes;
    @Getter
    private final DealsTypesExistsDataItem dealsTypes;

    public static ExistsDataController getOrCreate(IdentityStrategy identityStrategy) throws SQLException, DevException, PropertyVetoException {
        var result = controllers.get(identityStrategy);
        if (result == null) {
            result = new ExistsDataController(identityStrategy);
            controllers.put(identityStrategy, result);
        }
        return result;
    }

    private ExistsDataController(IdentityStrategy identityStrategy) throws SQLException, DevException, PropertyVetoException {
        this.identityStrategy = identityStrategy;
        var defaultTestItem = TestUtils.createDefaultTestItem(identityStrategy);
        try {
            persons = new PersonsExistsDataItem(defaultTestItem, PERSONS_SIZE);
            statusesTypes = new StatusesTypesExistsDataItem(defaultTestItem, STATUSES_SIZE);
            dealsTypes = new DealsTypesExistsDataItem(defaultTestItem, TYPES_SIZE);
        } finally {
            defaultTestItem.close();
        }
    }

    public void populateDeals(int dealsSize) throws SQLException, DevException, PropertyVetoException {
        if (dealsSize > existsDealsGUIds.size()) {
            existsDealsGUIds.clear();
            var item = TestUtils.createDefaultTestItem(identityStrategy);
            try {
                existsDealsGUIds.addAll(item.findDealsGUIds(dealsSize));
            } finally {
                item.close();
            }
        }
    }

    public UUID getDealGuid(int index) {
        return existsDealsGUIds.get(index);
    }
}