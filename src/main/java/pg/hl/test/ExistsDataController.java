package pg.hl.test;

import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.Person;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ExistsDataController {
    private static final Integer personsSize = 1000;
    private static final Integer statusesSize = 100;

    private static final List<UUID> existsDealsGUIds = new ArrayList<>();
    private static final Map<UUID, Integer> persons = new HashMap<>();
    private static UUID[] personsGuids = new UUID[0];
    private static final Map<String, Character> statusesTypes = new HashMap<>();
    private static String[] statusesTypesCodes = new String[0];
    private static final Random RANDOM = new Random();
    private static boolean inited = false;

    public static void init() throws Exception {
        if (inited) {
            return;
        }

        try (var defaultTestItem = TestUtils.createDefaultTestItem()) {
            var sourcePersons = defaultTestItem.findPersons(Integer.MAX_VALUE);
            if (sourcePersons.size() == 0) {
                sourcePersons = TestUtils.EASY_RANDOM.objects(Person.class, personsSize).collect(Collectors.toList());
                defaultTestItem.save(sourcePersons);
            }

            for (var item : sourcePersons) {
                persons.put(item.getGuid(), item.getId());
            }

            personsGuids = persons.keySet().toArray(new UUID[0]);
            if (personsGuids.length <= 0) {
                throw new Exception("Персоны отсутствуют в БД!");
            }

            var sourceStatusesTypes = defaultTestItem.findStatusesTypes(Integer.MAX_VALUE);
            if (sourceStatusesTypes.size() == 0) {
                sourceStatusesTypes = TestUtils.EASY_RANDOM.objects(ExchangeDealStatusType.class, statusesSize).collect(Collectors.toList());
                defaultTestItem.save(sourceStatusesTypes);
            }

            for (var item : sourceStatusesTypes) {
                statusesTypes.put(item.getCode(), item.getId());
            }

            statusesTypesCodes = statusesTypes.keySet().toArray(new String[0]);
            if (statusesTypesCodes.length <= 0) {
                throw new Exception("Статусы отсутствуют в БД!");
            }
        }

        inited = true;
    }

    public static void populateDeals(int dealsSize) throws SQLException {
        if (dealsSize > existsDealsGUIds.size()) {
            existsDealsGUIds.clear();
            try (var item = TestUtils.createDefaultTestItem()) {
                var deals = item.findDeals(dealsSize);
                for (var deal : deals) {
                    existsDealsGUIds.add(deal.getGuid());
                }
            }
        }
    }

    public static UUID getDealGuid(int index) {
        return existsDealsGUIds.get(index);
    }

    public static UUID getRandomPersonGuid() {
        return getRandomItem(personsGuids);
    }

    public static String getRandomStatusCode() {
        return getRandomItem(statusesTypesCodes);
    }

    public static Character resolveStatusType(String code) {
        return statusesTypes.get(code);
    }

    public static Integer resolvePerson(UUID guid) {
        return persons.get(guid);
    }

    private static <T> T getRandomItem(T[] source) {
        return source[RANDOM.nextInt(source.length - 1)];
    }
}
