package pg.hl.test;

import pg.hl.DevException;
import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.Person;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ExistsDataController {
    private static final Integer PERSONS_SIZE = 1000;
    private static final Integer STATUSES_SIZE = 100;
    private static final Integer MAX_RANDOM_COUNT = 100;

    private static final Random RANDOM = new Random();
    private static final Map<IdentityStrategy, ExistsDataController> controllers = new HashMap<>();

    private final Map<Object, Set<Object>> usedRandomValuesRepository = new HashMap<>();
    private final IdentityStrategy identityStrategy;
    private final List<UUID> existsDealsGUIds = new ArrayList<>();
    private final Map<UUID, Integer> persons = new HashMap<>();
    private final UUID[] personsGuids;
    private final Map<String, Character> statusesTypes = new HashMap<>();
    private final String[] statusesTypesCodes;

    public static ExistsDataController getOrCreate(IdentityStrategy identityStrategy) throws SQLException, DevException {
        var result = controllers.get(identityStrategy);
        if (result == null) {
            result = new ExistsDataController(identityStrategy);
            controllers.put(identityStrategy, result);
        }
        return result;
    }

    private ExistsDataController(IdentityStrategy identityStrategy) throws SQLException, DevException {
        this.identityStrategy = identityStrategy;
        try (var defaultTestItem = TestUtils.createDefaultTestItem(identityStrategy)) {
            var sourcePersons = defaultTestItem.findPersons(Integer.MAX_VALUE);
            if (sourcePersons.size() == 0) {
                sourcePersons = TestUtils.EASY_RANDOM.objects(Person.class, PERSONS_SIZE).collect(Collectors.toList());
                defaultTestItem.save(sourcePersons);
            }

            for (var item : sourcePersons) {
                persons.put(item.getGuid(), item.getId());
            }

            personsGuids = persons.keySet().toArray(new UUID[0]);
            if (personsGuids.length <= 0) {
                throw new DevException("Персоны отсутствуют в БД!");
            }

            var sourceStatusesTypes = defaultTestItem.findStatusesTypes(Integer.MAX_VALUE);
            if (sourceStatusesTypes.size() == 0) {
                var index = new AtomicInteger(65);
                sourceStatusesTypes = TestUtils.EASY_RANDOM.objects(ExchangeDealStatusType.class, STATUSES_SIZE)
                        .peek(p -> p.setId((char) index.getAndIncrement()))
                        .collect(Collectors.toList());
                defaultTestItem.save(sourceStatusesTypes);
            }

            for (var item : sourceStatusesTypes) {
                statusesTypes.put(item.getCode(), item.getId());
            }

            statusesTypesCodes = statusesTypes.keySet().toArray(new String[0]);
            if (statusesTypesCodes.length <= 0) {
                throw new DevException("Статусы отсутствуют в БД!");
            }
        }
    }

    public void populateDeals(int dealsSize) throws SQLException, DevException {
        if (dealsSize > existsDealsGUIds.size()) {
            existsDealsGUIds.clear();
            try (var item = TestUtils.createDefaultTestItem(identityStrategy)) {
                existsDealsGUIds.addAll(item.findDealsGUIds(dealsSize));
            }
        }
    }

    public UUID getDealGuid(int index) {
        return existsDealsGUIds.get(index);
    }

    public UUID getRandomPersonGuid(Object uniqueKey) {
        return getRandomItem(personsGuids, uniqueKey);
    }

    public String getRandomStatusCode(Object uniqueKey) {
        return getRandomItem(statusesTypesCodes, uniqueKey);
    }

    public Character resolveStatusTypeId(String code) {
        return statusesTypes.get(code);
    }

    public Integer resolvePersonId(UUID guid) {
        return persons.get(guid);
    }

    private <T> T getRandomItem(T[] source, Object uniqueKey) throws DevException {
        Set<Object> usedRandomValues = usedRandomValuesRepository.computeIfAbsent(uniqueKey, k -> new HashSet<>());

        for (int i = 0; i < MAX_RANDOM_COUNT; i++) {
            T result = source[RANDOM.nextInt(source.length - 1)];
            if (usedRandomValues.add(result)){
                return result;
            }
        }

        throw new DevException("Не удалось получить случайное уникальное значение");
    }
}
