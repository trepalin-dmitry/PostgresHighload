package pg.hl.test.edc;

import lombok.Getter;
import pg.hl.DevException;
import pg.hl.test.EntityType;
import pg.hl.test.IdentityStrategy;
import pg.hl.test.TestUtils;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class ExistsDataController {
    private static final Integer PERSONS_SIZE = 1000;
    private static final Integer STATUSES_SIZE = 100;
    private static final Integer TYPES_SIZE = 100;

    private static final Map<IdentityStrategy, ExistsDataController> controllers = new HashMap<>();
    private final IdentityStrategy identityStrategy;
    private final EntityType entityType;
    private final List<UUID> existsDealsGUIds = new ArrayList<>();

    @Getter
    private final PersonsExistsDataItem persons;
    @Getter
    private final StatusesTypesExistsDataItem statusesTypes;
    @Getter
    private final DealsTypesExistsDataItem dealsTypes;

    public static ExistsDataController getOrCreate(IdentityStrategy identityStrategy, EntityType entityType) throws SQLException, DevException, PropertyVetoException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var result = controllers.get(identityStrategy);
        if (result == null) {
            result = new ExistsDataController(identityStrategy, entityType);
            controllers.put(identityStrategy, result);
        }
        return result;
    }

    private ExistsDataController(IdentityStrategy identityStrategy, EntityType entityType) throws SQLException, DevException, PropertyVetoException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        this.identityStrategy = identityStrategy;
        this.entityType = entityType;
        var databaseHelper = TestUtils.createDatabaseHelper(identityStrategy, entityType);
        try {
            persons = new PersonsExistsDataItem(databaseHelper, PERSONS_SIZE);
            statusesTypes = new StatusesTypesExistsDataItem(databaseHelper, STATUSES_SIZE);
            dealsTypes = new DealsTypesExistsDataItem(databaseHelper, TYPES_SIZE);
        } finally {
            databaseHelper.close();
        }
    }

    public void populateDeals(int dealsSize) throws SQLException, DevException, PropertyVetoException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        if (dealsSize > existsDealsGUIds.size()) {
            existsDealsGUIds.clear();
            var databaseHelper = TestUtils.createDatabaseHelper(identityStrategy, entityType);
            try {
                existsDealsGUIds.addAll(databaseHelper.findDealsGUIds(dealsSize));
            } finally {
                databaseHelper.close();
            }
        }
    }

    public UUID getDealGuid(int index) {
        return existsDealsGUIds.get(index);
    }
}